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


import static org.logic2j.engine.solver.Continuation.CONTINUE;

import java.util.function.Function;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.unify.UnifyContext;

/**
 * A {@link SolutionListener} that forwards all solutions to another {@link SolutionListener},
 * but invoke intercepting methods before and after solutions are generated.
 * The intercepting methods can also control the continuation.
 */
public class InterceptorSolutionListener implements SolutionListener {
  private final SolutionListener delegate;
  private final Function<UnifyContext, Integer> beforeForwarding;
  private final Function<UnifyContext, Integer> afterForwarding;

  /**
   *
   * @param delegate The {@link SolutionListener} to delegate to
   * @param beforeForwarding Function to invoke before forwarding solution. Null means none.
   * @param afterForwarding Function to invoke after forwarding solution. Null means none.
   */
  public InterceptorSolutionListener(SolutionListener delegate,
                                     Function<UnifyContext, Integer> beforeForwarding,
                                     Function<UnifyContext, Integer> afterForwarding) {
    this.delegate = delegate;
    this.beforeForwarding = beforeForwarding;
    this.afterForwarding = afterForwarding;
  }

  @Override
  public int onSolution(UnifyContext currentVars) {
    if (beforeForwarding != null) {
      final Integer cont = beforeForwarding.apply(currentVars);
      if (cont != Continuation.CONTINUE) {
        return cont;
      }
    }

    final int notif = delegate.onSolution(currentVars);
    if (notif != CONTINUE) {
      return notif;
    }

    if (afterForwarding != null) {
      final Integer cont = afterForwarding.apply(currentVars);
      if (cont != Continuation.CONTINUE) {
        return cont;
      }
    }

    return notif;
  }

}
