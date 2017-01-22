package org.logic2j.predsolver.predicates.impl;


import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

import java.util.function.Predicate;

/**
 * Only check reified values against a {@link java.util.function.Predicate},
 * not able to bind a free {@link Var}.
 */
public abstract class TestingFOPredicate1<T> extends FOPredicate implements Predicate<T> {

  public TestingFOPredicate1(String functorName, T term) {
    super(functorName, term);
  }

  public TestingFOPredicate1(String functorName, Var<T> term) {
    super(functorName, term);
  }

  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object reified = currentVars.reify(getArg(0));
    if (this.test((T)reified)) {
      notifySolution(theListener, currentVars);
    }
    return Continuation.CONTINUE;
  }
}