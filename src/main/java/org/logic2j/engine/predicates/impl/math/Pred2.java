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
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

import java.util.Arrays;
import java.util.function.Function;

/**
 * 2-arguments predicates with a functional relation between the two argument(s),
 * could be a bijection functions, or any mapping actually.
 */
public class Pred2<T, R> extends FOPredicate {

  private Function<T, R> image = v -> {
    throw new UnsupportedOperationException("Function \"image()\" of predicate " + Pred2.this + " is not " + "implemented");
  };
  private Function<T, R[]> images = v -> (R[]) new Object[] {image.apply(v)};
  private Function<R, T> preimage = v -> {
    throw new UnsupportedOperationException("Function \"preimage()\" of predicate " + Pred2.this + " is not " + "implemented");
  };
  private Function<R, T[]> preimages = v -> (T[]) new Object[] {preimage.apply(v)};


  /**
   * A binary predicate with two functions defining the forward and reverse mappings.
   *
   * @param theFunctor
   */
  public Pred2(String theFunctor, Binding<T> arg0, Binding<R> arg1) {
    super(theFunctor, arg0, arg1);
  }


  @Override
  public final Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object n0 = currentVars.reify(getArg(0));
    final Object n1 = currentVars.reify(getArg(1));

    if (isConstant(n0)) {
      if (isConstant(n1)) {
        for (T c0 : this.<T>constants(n0)) {
          for (R c1 : this.<R>constants(n1)) {
            // Both bound values - check
            final R[] images = this.images.apply(c0);
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
        final Object[] images = Arrays.stream(this.<T>constants(n0)).map(this.images).flatMap(Arrays::stream).toArray(Object[]::new);
        return unifyAndNotifyMany(theListener, currentVars, n1, images);
      }
    }

    if (isFreeVar(n0)) {
      // n0 is a free variable, unify in reverse direction
      if (isConstant(n1)) {
        final Object[] preimages = Arrays.stream(this.<R>constants(n1)).map(this.preimages).flatMap(Arrays::stream).toArray(Object[]::new);
        return unifyAndNotifyMany(theListener, currentVars, n0, preimages);
      } else {
        // Two free variables - no solution
        return Continuation.CONTINUE;
      }
    }
    throw new SolverException("Should never be here");
  }

  // --------------------------------------------------------------------------
  // Fluent setters
  // --------------------------------------------------------------------------


  public Pred2<T, R> withImage(Function<T, R> image) {
    this.image = image;
    return this;
  }

  public Pred2<T, R> withImages(Function<T, R[]> images) {
    this.images = images;
    return this;
  }

  public Pred2<T, R> withPreimage(Function<R, T> preimage) {
    this.preimage = preimage;
    return this;
  }

  public Pred2<T, R> withPreimages(Function<R, T[]> preimages) {
    this.preimages = preimages;
    return this;
  }

  // --------------------------------------------------------------------------
  // Accessors
  // --------------------------------------------------------------------------

  public Function<T, R> getImage() {
    return image;
  }

  public void setImage(Function<T, R> image) {
    this.image = image;
  }

  public Function<T, R[]> getImages() {
    return images;
  }

  public void setImages(Function<T, R[]> images) {
    this.images = images;
  }

  public Function<R, T> getPreimage() {
    return preimage;
  }

  public void setPreimage(Function<R, T> preimage) {
    this.preimage = preimage;
  }

  public Function<R, T[]> getPreimages() {
    return preimages;
  }

  public void setPreimages(Function<R, T[]> preimages) {
    this.preimages = preimages;
  }
}
