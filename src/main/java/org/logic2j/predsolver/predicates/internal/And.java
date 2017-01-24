package org.logic2j.predsolver.predicates.internal;

import org.logic2j.predsolver.model.Struct;
import org.logic2j.predsolver.model.Term;

/**
 * Logical AND.
 * The implementation is hard-coded in the Solver, hence we do not provided it here.
 */
public class And extends Struct {
  public And(Term... conjunctions) {
    super(FUNCTOR_COMMA, (Object[]) conjunctions);
  }

}
