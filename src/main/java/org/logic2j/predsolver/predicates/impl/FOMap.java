package org.logic2j.predsolver.predicates.impl;


import org.logic2j.predsolver.model.Var;
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