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
import org.logic2j.engine.model.Term;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.unify.UnifyContext;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.logic2j.engine.solver.Continuation.CONTINUE;

/**
 * 2-arguments predicates with a functional relation between the two argument(s),
 * could be a bijection functions, or any mapping actually.
 */
public class Pred2<T, R> extends FOPredicate {

  private Function<T, R> image = v -> {
    throw new UnsupportedOperationException("Function \"image()\" of " + Pred2.this + " was not defined");
  };
  private Function<T, R[]> images = v -> (R[]) new Object[] {image.apply(v)};
  private Function<R, T> preimage = v -> {
    throw new UnsupportedOperationException("Function \"preimage()\" of " + Pred2.this + " was not defined");
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

  // For equalling terms, eg. eq(X, or(...)), we cannot wrap the constant term in a SimpleBinding
  // because the solution API can't find terms recursively in there.
  public Pred2(String theFunctor, Binding<Term> t1, Term t2) {
    super(theFunctor, t1, t2);
  }


  @Override
  public final int predicateLogic(UnifyContext currentVars) {
    final Object n0 = currentVars.reify(getArg(0));
    final Object n1 = currentVars.reify(getArg(1));

    if (this.image == null && isFreeVar(n1)) {
      // If function is not defined, won't need to find solution(s) for free preimage
      return CONTINUE;
    }

    if (this.preimage == null && isFreeVar(n0)) {
      // If function is not invertible, won't need to find solution(s) for free image
      return CONTINUE;
    }

    return unification(currentVars, n0, n1);
  }

  protected int unification(UnifyContext currentVars, Object n0, Object n1) {
    if (isConstant(n0)) {
      if (isConstant(n1)) {
        final R[] values1 = FOPredicate.<R>stream(n1).toArray(n -> (R[]) new Object[n]);
        for (T c0 : (Iterable<T>) FOPredicate.<T>stream(n0)::iterator) {
          for (final R c1 : values1) {
            // Both bound values - check
            final R[] images = this.images.apply(c0);
            final boolean found = Arrays.stream(images).anyMatch(v -> v.equals(c1));
            final int continuation = notifySolutionIf(found, currentVars);
            if (continuation != CONTINUE) {
              return continuation;
            }
          }
        }
        return CONTINUE;
      } else {
        // n1 is free, just unify in forward direction
        final Stream<R> images = FOPredicate.<T>stream(n0).map(this.images).flatMap(Arrays::stream);
        return unifyAndNotifyMany(currentVars, n1, images);
      }
    }

    if (isFreeVar(n0)) {
      // n0 is a free variable, unify in reverse direction
      if (isConstant(n1)) {
        final Stream<T> preimages = FOPredicate.<R>stream(n1).map(this.preimages).flatMap(Arrays::stream);
        return unifyAndNotifyMany(currentVars, n0, preimages);
      } else {
        // Two free variables - no solution
        return CONTINUE;
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
