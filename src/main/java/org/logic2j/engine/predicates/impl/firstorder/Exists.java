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

package org.logic2j.engine.predicates.impl.firstorder;

import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.predicates.external.RDBCompatiblePredicate;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.solver.Solver;
import org.logic2j.engine.solver.listener.ExistsSolutionListener;
import org.logic2j.engine.unify.UnifyContext;

/**
 * Prove existence (common 1-arity form) or bind result of existence or inexistence demonstration (2-arity form).
 * In the 1-arity form, will try to demonstrate the specified goal up to its first solution
 * only (no other is sought); succeed if so, fails if the specified goal does not provide a single one.
 * In the 2-arity form, will expect a bound boolean value and prove existence if true or prove inexistence in
 * false, or if the second argument is free, will bind the result of the demonstration.
 */
public class Exists extends FOPredicate implements RDBCompatiblePredicate {

  /**
   * Succeeds if theGoal provides at least one solution.
   * Notice that theGoal will be solved up to only its first
   * solution, so that enumeration will not be completed, see {@link ExistsSolutionListener}.
   *
   * @param theGoal
   */
  public Exists(Term theGoal) {
    super("exists", theGoal);
  }

  /**
   * If booleanResult is bound will succeed if existence of theGoal can be demonstrated (up to the first solution).
   * If booleanResult is unbound, will always succeed, and bind the booleanResult to true in case at least
   * one solution of theGoal exists, false otherwise.
   * Notice that theGoal will be solved up to only its first
   * solution, so that enumeration will not be completed, see {@link ExistsSolutionListener}.
   *
   * @param theGoal
   * @param booleanResult
   */
  public Exists(Term theGoal, Binding<Boolean> booleanResult) {
    super("exists", theGoal, booleanResult);
  }

  @Override
  public int predicateLogic(UnifyContext currentVars) {

    // Solve against a minimal SolutionListener just interested on the first solution
    final ExistsSolutionListener seekOnlyTheFirstSolution = new ExistsSolutionListener();
    final Solver solver = currentVars.getSolver();
    solver.solveGoal(getArg(0), currentVars.withListener(seekOnlyTheFirstSolution));
    final boolean exists = seekOnlyTheFirstSolution.exists();

    return switch (getArity()) {
      case 1 ->
        // Normal use, the predicate will succeed with one solution if theGoal was proven to provide at least
        // one solution. Otherwise, will fail.
              notifySolutionIf(exists, currentVars);
      case 2 ->
        // Alternate signature: will unify the proof of existence with the second argument.
              unifyAndNotify(currentVars, exists, getArg(1));
      default -> throw new SolverException("Illegal arity to " + this);
    };

  }


}
