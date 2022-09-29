/*
 * logic2j - "Bring Logic to your Java" - Copyright (c) 2017 Laurent.Tettoni@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.logic2j.engine.predicates.internal;

import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.predicates.Predicates.conjunction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.logic2j.engine.exception.InvalidTermException;
import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.solver.listener.InterceptorSolutionListener;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

/**
 * optional/1   optional(goal)
 * Will find all solutions to the goal.
 * If there are 1,2,...,N solutions they will be relayed to the SolutionListener.
 * In case there is no solution this goal will provide one successful solution.
 */
public class Optional extends SolverPredicate {
  public Optional(Term goals) {
    super("optional", conjunction(goals));
  }

  @Override
  public int predicateLogic(UnifyContext currentVars, int cutLevel) {
    return callLogic(this, currentVars, cutLevel);
  }

  public static int callLogic(Struct<?> goalStruct, UnifyContext currentVars, int cutLevel) {
    final int arity = goalStruct.getArity();
    if (arity != 1) {
      throw new InvalidTermException("Primitive \"optional\" accepts only one argument, got " + arity);
    }
    final Object callTerm = goalStruct.getArg(0);  // Often a Var
    final Object reifiedGoal = currentVars.reify(callTerm); // The real value of the Var
    if (termApi().isFreeVar(reifiedGoal)) {
      throw new SolverException("Cannot optional/* on a free variable");
    }


    // Solutions will go through this delegating listener, with side effect
    final AtomicBoolean solutionHit = new AtomicBoolean(false);
    final Function<UnifyContext, Integer> detectSolutions = (uc) -> {
      solutionHit.set(true);
      return Continuation.CONTINUE;
    };
    final SolutionListener solutionListenerProxy = new InterceptorSolutionListener(currentVars.getSolutionListener(), null, detectSolutions);

    // Now solve the goal, it may provide 0,1,N solutions (and we are not aware of, except through the callback above)
    int cont = currentVars.getSolver().solveInternalRecursive(reifiedGoal, currentVars.withListener(solutionListenerProxy), cutLevel);

    if (! solutionHit.get()) {
      // There was no solution, so we provide one (without binding variables)
      cont = currentVars.getSolutionListener().onSolution(currentVars);
    }
    return cont;
  }


}
