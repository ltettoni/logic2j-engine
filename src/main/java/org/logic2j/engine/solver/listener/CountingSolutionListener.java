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


import org.logic2j.engine.unify.UnifyContext;

import static org.logic2j.engine.solver.Continuation.CONTINUE;
import static org.logic2j.engine.solver.Continuation.USER_ABORT;

/**
 * A base implementation of {@link SolutionListener} that holds a counter of the number of solutions reached.
 * The {@link #onSolution(UnifyContext)} method always returns Continuation.CONTINUE (dangerously allowing for potential
 * infinite generation). Derive from this class to ease the programming of
 * {@link SolutionListener}s in application code, and DO NOT FORGET to call super.onSolution() in order to count!
 */
public class CountingSolutionListener implements SolutionListener {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CountingSolutionListener.class);

  /**
   * Number of solutions (so far).
   */
  private int count = 0;

  /**
   * Will stop counting (hence stop fetching new solutions) after that number of iterations.
   */
  private final long maxIteration;

  /**
   * Solve by counting the number of solution up to the last one.
   */
  public CountingSolutionListener() {
    this(Long.MAX_VALUE); // Virtually: infinite
  }

  public CountingSolutionListener(long maxIteration) {
    this.maxIteration = maxIteration;
  }

  @Override
  public int onSolution(UnifyContext currentVars) {
    this.count++;
    if (logger.isDebugEnabled()) {
      logger.debug(" onSolution(#{})", this.count);
    }
    if (this.count >= this.maxIteration) {
      return USER_ABORT;
    }
    return CONTINUE;
  }

  // ---------------------------------------------------------------------------
  // Accessors
  // ---------------------------------------------------------------------------

  /**
   * @return the total number of solutions that were demonstrated (to the last one).
   */
  public int count() {
    return this.count;
  }

  /**
   * This is not an efficient way of proving existence, unless this object was instantiated
   * with maxIteration=1; rather look for {@link ExistsSolutionListener}
   *
   * @return true of there was at least one solution demonstrated.
   */
  public boolean exists() {
    return this.count > 0;
  }

}
