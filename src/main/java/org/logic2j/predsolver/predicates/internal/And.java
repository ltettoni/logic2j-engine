package org.logic2j.predsolver.predicates.internal;

import org.logic2j.predsolver.model.Struct;
import org.logic2j.predsolver.model.Term;

/**
 * Does not provide any solution.
 */
public class And extends Struct {
  public And(Term... conjunctions) {
    super(FUNCTOR_COMMA, (Object[])conjunctions);
  }

}
