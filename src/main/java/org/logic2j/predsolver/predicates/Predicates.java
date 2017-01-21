package org.logic2j.predsolver.predicates;

import org.logic2j.predsolver.model.Term;

/**
 * Factory methods for common predicates.
 */
public final class Predicates {

  public static final True ttrue = new True();
  public static final Fail fail = new Fail();

  public static And and(Term... conjunctions) {
    return new And(conjunctions);
  }

  public static Or or(Term... disjunctions) {
    return new Or(disjunctions);
  }

}
