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

package org.logic2j.predsolver.predicates.impl.java;


import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.impl.FOPredicate;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

import java.util.function.Function;

/**
 * Bridge between first-order predicates and plain Java functions for mapping single values.
 * <p>
 * Only applies reified values against a {@link Function},
 * not able to bind a free {@link Var}.
 */
public class FOMap<T, R> extends FOPredicate {

  private final Function<T, R> javaFunction;

  public FOMap(Var<T> v1, Function<T, R> javaFunction, Var<R> v2) {
    super("_javaLambaFunction", v1, v2);
    this.javaFunction = javaFunction;
  }


  @Override
  public final Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object reified = currentVars.reify(getArg(0));
    if (reified != null && !(reified instanceof Var)) {
      final R result = this.javaFunction.apply((T) reified);
      unifyAndNotify(theListener, currentVars, getArg(1), result);
    }
    return Continuation.CONTINUE;
  }
}