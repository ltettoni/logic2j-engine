package org.logic2j.predsolver.predicates.impl;


import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

import java.util.function.Predicate;

/**
 * Bridge between first-order predicates and plain Java predicates for checking single values.
 * <p>
 * Only check reified values against a {@link java.util.function.Predicate},
 * not able to bind a free {@link Var}.
 */
public abstract class FOPredicateJavaPredicate<T> extends FOPredicate {

  private final Predicate<T> javaPredicate;

  public FOPredicateJavaPredicate(String functorName, T term, Predicate<T> javaPredicate) {
    super(functorName, term);
    this.javaPredicate = javaPredicate;
  }

  public FOPredicateJavaPredicate(String functorName, Var<T> term, Predicate<T> javaPredicate) {
    super(functorName, term);
    this.javaPredicate = javaPredicate;
  }

  @Override
  public final Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object reified = currentVars.reify(getArg(0));
    if (reified != null && !(reified instanceof Var)) {
      if (this.javaPredicate.test((T) reified)) {
        notifySolution(theListener, currentVars);
      }
    }
    return Continuation.CONTINUE;
  }
}