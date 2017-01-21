package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.model.Struct;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Does not provide any solution.
 */
public abstract class Predicate extends Struct {

  public Predicate(String theFunctor, Object... argList) {
    super(theFunctor, argList);
  }

  /**
   * All predicates need to implement the invocation function
   *
   * @return PrimitiveType#PREDICATE
   */
  @Override
  public PrimitiveType getPrimitiveType() {
    return PrimitiveType.PREDICATE;
  }

  /**
   * Notify theSolutionListener that a solution has been found.
   *
   * @param theSolutionListener
   * @return The {@link Continuation} as returned by theSolutionListener's {@link SolutionListener#onSolution(UnifyContext)}
   */
  protected Integer notifySolution(SolutionListener theSolutionListener, UnifyContext currentVars) {
    final Integer continuation = theSolutionListener.onSolution(currentVars);
    return continuation;
  }

  /**
   * Unify terms t1 and t2, and if they could be unified, call theListener with the solution of the newly
   * unified variables; return the result from notifying. If not, return CONTINUE.
   * @param theListener
   * @param currentVars
   * @param t1
   * @param t2
   * @return
   */
  protected Integer unifyAndNotify(SolutionListener theListener, UnifyContext currentVars, Object t1, Object t2) {
    final UnifyContext after = currentVars.unify(t1, t2);
    if (after == null) {
      // Not unified: do not notify a solution and inform to continue solving
      return Continuation.CONTINUE;
    }
    // Unified
    return notifySolution(theListener, after);
  }

  // ---------------------------------------------------------------------------
  // The logic of this predicate
  // ---------------------------------------------------------------------------

  /**
   * Invoked by the {@link org.logic2j.predsolver.solver.Solver}.
   * @param theListener
   * @param currentVars
   * @return The continuation, one of {@link org.logic2j.predsolver.solver.Continuation} values.
   */
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    throw new UnsupportedOperationException("The base Struct.invoke() method does not define any logic: class Struct must be "
        + "derived. Instance was: \"" +
        this + '"');
  }

}
