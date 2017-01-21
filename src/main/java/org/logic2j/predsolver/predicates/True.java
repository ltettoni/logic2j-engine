package org.logic2j.predsolver.predicates;

import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Does not provide any solution.
 */
public class True extends Predicate {
  public True() {
    super("true");
  }

  @Override
  public Integer invokePredicate(UnifyContext currentVars, SolutionListener theListener) {
    return notifySolution(theListener, currentVars);
  }
}
