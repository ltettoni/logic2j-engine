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

import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.unify.UnifyContext;

/**
 * Provides one solution and cuts backtracking.
 * When a CUT appears in a clause, it will succeed with one solution and prevent the solver from:
 * (i) bactracking for other solutions to any terms that precede the CUT in the same clause,
 * and (ii) considering any further matching clause (fact or
 * rule) for continuing to solve the parent (calling) goal.
 *
 * Algorithm:
 * - Each time the solver starts iterating from the first clause (from a Prolog theory) in order to prove a goal,
 *   the recursion counter "cutLevel" is incremented. Solving the initial query starts with cutLevel=1.
 * - Executing the CUT will invoke the current SolutionListener a single time, without binding any variable.
 * - The ex
 *
 *
 * Note: The implementation is also hard-coded in the Solver.
 */
public class Cut extends SolverPredicate {
  public Cut() {
    super(FUNCTOR_CUT);
  }

  @Override
  public int predicateLogic(UnifyContext currentVars, int cutLevel) {
    return cutLogic(currentVars, cutLevel);
  }

  /**
   * This is a "native" implementation of CUT: we are currently executing a "!" predicate to break backtracking.
   *
   * @param currentVars
   * @param cutLevel
   * @return Usually will return cutLevel >= 1, meaning caller must stop searching for alternatives.
   */
  public static int cutLogic(UnifyContext currentVars, int cutLevel) {
    // Cut IS a valid solution in itself. We do not bind any variable but call a single solution.
    final int downstreamContinuation = currentVars.getSolutionListener().onSolution(currentVars);
    // When return from the solution above, this means we backtrack ! We must tell our caller that CUT was executed!

    if (downstreamContinuation == Continuation.CONTINUE || downstreamContinuation < 1) {
      // We are in a typical case: downstream wanted to continue but we will have to cut it.
      // Or downstream wanted to abort, in which case we cut it anyway that leads to the same.
      return cutLevel;
    } else {
      // We've got a cut from processing downstream solutions, we don't need to cut so return what we've received
      assert downstreamContinuation <= cutLevel;
      return downstreamContinuation;
    }
  }
}
