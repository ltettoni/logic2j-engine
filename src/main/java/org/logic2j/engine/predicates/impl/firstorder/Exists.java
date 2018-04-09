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
 * Succeeds if the specified goal provides a single solution (and none other is sought); fails if
 * the specified goal does not provide a single one.
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
   * Always succeeds, and binds the booleanResult to true in case at least one solution of theGoal exists, false
   * otherwise.
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

    switch (getArity()) {
      case 1:
        // Normal use, the predicate will succeed with one solution if theGoal was proven to provide at least
        // one solution. Otherwise will fail.
        return notifySolutionIf(exists, currentVars);
      case 2:
        // Alternate signature: will unify the proof of existence with the second argument.
        return unifyAndNotifyMany(currentVars, exists, (Binding<Boolean>) getArg(1));
      default:
        throw new SolverException("Illegal arity to " + this);
    }

  }


}
