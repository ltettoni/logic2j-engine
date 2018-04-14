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

import org.logic2j.engine.model.Struct;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.unify.UnifyContext;

/**
 * Provides one solution and cuts backtracking.
 * The implementation is hard-coded in the Solver, hence we do not provide it here.
 */
public class Cut extends Struct {
  public Cut() {
    super(FUNCTOR_CUT);
  }

  /**
   * This is a "native" implementation of CUT.
   *
   * @param goalStruct
   * @param currentVars
   * @param cutLevel
   * @return
   */
  public static int cutLogic(Struct goalStruct, UnifyContext currentVars, int cutLevel) {
    // Cut IS a valid solution in itself. We just ignore what the application asks (via return value) us to do next.
    final int continuationFromCaller =
        currentVars.getSolutionListener().onSolution(currentVars);// Signalling one valid solution, but ignoring return value

    final int result;
    if (continuationFromCaller != Continuation.CONTINUE && continuationFromCaller > 0) {
      result = continuationFromCaller;
    } else {
      // Stopping there for this iteration
      result = cutLevel;
    }
    return result;
  }
}
