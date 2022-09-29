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

import org.logic2j.engine.exception.InvalidTermException;
import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.unify.UnifyContext;

import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.predicates.Predicates.conjunction;

/**
 * Invoke the solver on a sub-goal or conjunction (AND) of sub-goals.
 * Sub-goals can be variables (they will be reified).
 */
public class Call extends SolverPredicate {
  public Call(Term... goals) {
    super(FUNCTOR_CALL, conjunction(goals));
  }

  @Override
  public int predicateLogic(UnifyContext currentVars, int cutLevel) {
    return callLogic(this, currentVars, cutLevel);
  }

  public static int callLogic(Struct<?> goalStruct, UnifyContext currentVars, int cutLevel) {
    final int arity = goalStruct.getArity();
    if (arity != 1) {
      throw new InvalidTermException("Primitive \"call\" accepts only one argument, got " + arity);
    }
    final Object callTerm = goalStruct.getArg(0);  // Often a Var
    final Object realCallTerm = currentVars.reify(callTerm); // The real value of the Var
    if (termApi().isFreeVar(realCallTerm)) {
      throw new SolverException("Cannot call/* on a free variable");
    }
    return currentVars.getSolver().solveInternalRecursive(realCallTerm, currentVars, cutLevel);
  }


}
