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
package org.logic2j.predsolver.solver.listener;


import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * A base implementation of {@link SolutionListener} that holds a counter of the number of solutions reached.
 * The {@link #onSolution(UnifyContext)} method always returns Continuation.CONTINUE (dangerously allowing for potential
 * infinite generation). Derive from this class to ease the programming of
 * {@link SolutionListener}s in application code, and DO NOT FORGET to call super.onSolution() in order to count!
 */
public class CountingSolutionListener implements SolutionListener {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CountingSolutionListener.class);
  private static final boolean DEBUG_ENABLED = logger.isDebugEnabled();

  /**
   * Number of solutions (so far).
   */
  private long count = 0;


  @Override
  public Integer onSolution(UnifyContext currentVars) {
    this.count++;
    if (DEBUG_ENABLED) {
      logger.debug(" onSolution(#{})", this.count);
    }
    return Continuation.CONTINUE;
  }

  // ---------------------------------------------------------------------------
  // Accessors
  // ---------------------------------------------------------------------------

  public long count() {
    return this.count;
  }

  public boolean exists() {
    return this.count > 0;
  }

}
