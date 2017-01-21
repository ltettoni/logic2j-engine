package org.logic2j.predsolver.predicates.impl;


import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Generate list of values in order.
 */
public abstract class ListOfValuesPredicate1<T> extends Predicate {

  private final T[] possibleValues;

  public ListOfValuesPredicate1(String functorName, T term, T... possibleValues) {
    super(functorName, term);
    this.possibleValues = possibleValues;
  }

  public ListOfValuesPredicate1(String functorName, Var<T> term, T... possibleValues) {
    super(functorName, term);
    this.possibleValues = possibleValues;
  }

  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object term = getArg(0);

    Integer continuation = Continuation.CONTINUE;
    for (T value : possibleValues) {
      continuation = unifyAndNotify(theListener, currentVars, term, value);
      if (continuation == Continuation.USER_ABORT) {
        return continuation;
      }
    }
    return continuation;
  }
}