package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.exception.InvalidTermException;
import org.logic2j.predsolver.model.Struct;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Does not provide any solution.
 */
public abstract class FOPredicate extends Struct {

  public FOPredicate(String theFunctor, Object... argList) {
    super(theFunctor, argList);
  }


  /**
   * Notify listener that a solution has been found.
   *
   * @param listener
   * @return The {@link Continuation} as returned by listener's {@link SolutionListener#onSolution(UnifyContext)}
   */
  protected Integer notifySolution(SolutionListener listener, UnifyContext currentVars) {
    final Integer continuation = listener.onSolution(currentVars);
    return continuation;
  }


  protected Integer notifySolutionIf(boolean condition, SolutionListener theListener, UnifyContext currentVars) {
    if (condition) {
      return notifySolution(theListener, currentVars);
    } else {
      return Continuation.CONTINUE;
    }
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
    final UnifyContext afterUnification = currentVars.unify(t1, t2);

    final boolean couldUnifySomething = afterUnification != null;
    return notifySolutionIf(couldUnifySomething, theListener, afterUnification);
  }

  /**
   * Make sure term is not a free {@link Var}.
   *
   * @param term
   * @param nameOfPrimitive Non functional - only to report the name of the primitive in case an Exception is thrown
   * @param indexOfArg zero-based index of argument causing error
   * @throws InvalidTermException
   */
  protected void ensureBindingIsNotAFreeVar(Object term, String nameOfPrimitive, int indexOfArg) {
    if (term instanceof Var) {
      // TODO Should be a kind of InvalidGoalException instead?
      final int positionOfArgument = indexOfArg + 1;
      throw new InvalidTermException("Cannot invoke primitive \"" + nameOfPrimitive + "\" with a free variable, check argument #" + positionOfArgument);
    }
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
  public abstract Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars);
//    throw new UnsupportedOperationException("The base Struct.invoke() method does not define any logic: class Struct must be "
//        + "derived. Instance was: \"" +
//        this + '"');

}
