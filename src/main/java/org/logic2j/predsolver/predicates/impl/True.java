package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Always succeeds, provides one solution but does not bind any variables.
 */
public class True extends FOPredicate {
  public True() {
    super("true");
  }

  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    return notifySolution(theListener, currentVars);
  }
}
