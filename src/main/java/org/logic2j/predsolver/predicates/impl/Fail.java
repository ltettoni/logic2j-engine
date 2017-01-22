package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Does not provide any solution.
 */
public class Fail extends FOPredicate {
  public Fail() {
    super("fail");
  }

  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    // Provide no solution
    return Continuation.CONTINUE;
  }
}
