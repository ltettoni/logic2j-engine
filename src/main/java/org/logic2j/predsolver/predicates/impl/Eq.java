package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Unification operator "=".
 */
public class Eq extends Predicate {
  public Eq(Object t1, Object t2) {
    super("=", t1, t2);
  }

  @Override
  public Integer invokePredicate(UnifyContext currentVars, SolutionListener theListener) {
    final Object[] args = getArgs();
    return unifyAndNotify(theListener, currentVars, args[0], args[1]);
  }
}
