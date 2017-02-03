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

package org.logic2j.engine.predicates.impl.math;

import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

import java.util.Arrays;

/**
 * 2-arguments predicates with a functional relation between the two argument(s),
 * could be a bijection functions, or any mapping actually.
 */
public abstract class Pred2 extends FOPredicate {
  /**
   * A binary predicate with two functions defining the forward and reverse mappings.
   *
   * @param theFunctor
   * @param argList
   */
  public Pred2(String theFunctor, Object... argList) {
    super(theFunctor, argList);
  }

  /**
   * The forward mapping function. If not a function, override rather {@link #images(Object)}
   * This method is never called directly, only through {@link #images(Object)}.
   * @param value
   * @return
   */
  protected abstract Object image(Object value);

  /**
   * By default our functions are real functions, ie. produce only one image.
   * Override this if this is not the case.
   * @param value
   * @return Array of images, in this base implementation only one.
   */
  protected Object[] images(Object value) {
    return new Object[] {image(value)};
  }

  /**
   * The reverse mapping function. If not a bijection, override rather {@link #preimages(Object)}
   * This method is never called directly, only through {@link #preimages(Object)}.
   * @param value
   * @return
   */
  protected abstract Object preimage(Object value);

  /**
   * By default our functions are real bijections, ie. produce only one preimage.
   * Override this if this is not the case.
   * @param value
   * @return Array of images, in this base implementation only one.
   */
  protected Object[] preimages(Object value) {
    return new Object[] {preimage(value)};
  }



  @Override
  public final Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object n0 = currentVars.reify(getArg(0));
    final Object n1 = currentVars.reify(getArg(1));

    if (isConstant(n0)) {
      if (isConstant(n1)) {
        for (Object c0 : constants(n0)) {
          for (Object c1 : constants(n1)) {
            // Both bound values - check
            final Object[] images = images(c0);
            final boolean found = Arrays.stream(images).anyMatch(v -> v.equals(c1));
            final int cont = notifySolutionIf(found, theListener, currentVars);
            if (cont != Continuation.CONTINUE) {
              return cont;
            }
          }
        }
        return Continuation.CONTINUE;
      } else {
        // n1 is free, just unify in forward direction
        final Object[] images = Arrays.stream(constants(n0)).map(this::images).flatMap(Arrays::stream).toArray(Object[]::new);
        return unifyAndNotifyMany(theListener, currentVars, n1, images);
      }
    }

    if (isFreeVar(n0)) {
      // n0 is a free variable, unify in reverse direction
      if (isConstant(n1)) {
        final Object[] preimages = Arrays.stream(constants(n1)).map(this::preimages).flatMap(Arrays::stream).toArray(Object[]::new);
        return unifyAndNotifyMany(theListener, currentVars, n0, preimages);
      } else {
        // Two free variables - no solution
        return Continuation.CONTINUE;
      }
    }
    throw new SolverException("Should never be here");
  }

}
