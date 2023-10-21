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

package org.logic2j.engine.solver;

import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

/**
 * Usual codes that applications or libraries should return,
 * that specify which behaviour the inference engine should take after
 * a solution was found, via
 * {@link SolutionListener#onSolution(UnifyContext)}.
 * <p/>
 * Continuations are int constants.
 * Several methods return such an int whose interpretation is a {@link Continuation}, and most often one of the two
 * constants defined below are returned; however in the case of {@link org.logic2j.engine.predicates.internal.Cut},
 * a value > 0 can be returned too.
 * See documentation in {@link SolutionListener}.
 *
 * @author tettoni
 */
public final class Continuation {

  private Continuation() {
    // Forbid instantiation
  }

  /**
   * Value that {@link SolutionListener#onSolution(UnifyContext)}
   * must return for the inference engine to continue solving (searching for alternate solutions).
   */
  public static final int CONTINUE = 0;

  /**
   * Value that {@link SolutionListener#onSolution(UnifyContext)}
   * must return for the inference engine to abort solving (i.e. means aborting upon explicit request).
   */
  public static final int USER_ABORT = -1;

}
