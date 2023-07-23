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

package org.logic2j.engine.solver.listener;


import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.unify.UnifyContext;

/**
 * The simplest {@link SolutionListener} that only checks existence of the first solution, and then aborts execution of subsequent ones.
 * Watch out, due to aborting execution after the first solution, there may be less execution that you might expect. The side effect
 * is similar to evaluating a function in the middle of logical ANDs: previous results may not necessitate further executions.
 *
 * Note: {@link CountingSolutionListener} with a max number of solutions of 1 offers the same functionality. But this code is dead simple.
 */
public class ExistsSolutionListener implements SolutionListener {
  private boolean atLeastOneSolution = false;

  @Override
  public int onSolution(UnifyContext currentVars) {
    // Do NOT relay the solution further, just remember there was one
    this.atLeastOneSolution = true;
    // No need to seek for further solutions. Watch out this means the goal will stop evaluating on first success!
    return Continuation.USER_ABORT;
  }

  public boolean exists() {
    return atLeastOneSolution;
  }
}
