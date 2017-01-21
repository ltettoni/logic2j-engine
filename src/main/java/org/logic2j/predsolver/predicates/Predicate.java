package org.logic2j.predsolver.predicates;

import org.logic2j.predsolver.exception.InvalidTermException;
import org.logic2j.predsolver.model.Struct;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Does not provide any solution.
 */
public abstract class Predicate extends Struct {

  public Predicate(String theFunctor, Object... argList) throws InvalidTermException {
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

  // ---------------------------------------------------------------------------
  // The logic of this predicate
  // ---------------------------------------------------------------------------

  /**
   * Invoked by the {@link org.logic2j.predsolver.solver.Solver}.
   * @param currentVars
   * @param theListener
   * @return The continuation, one of {@link org.logic2j.predsolver.solver.Continuation} values.
   */
  public Integer invokePredicate(UnifyContext currentVars, SolutionListener theListener) {
    throw new UnsupportedOperationException("The base Struct.invoke() method does not define any logic: class Struct must be "
        + "derived. Instance was: \"" +
        this + '"');
  }

}
