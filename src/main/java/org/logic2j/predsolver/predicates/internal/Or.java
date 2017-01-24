package org.logic2j.predsolver.predicates.internal;

import org.logic2j.predsolver.model.Struct;
import org.logic2j.predsolver.model.Term;

/**
 * Logical OR.
 * The implementation is hard-coded in the Solver, hence we do not provided it here.
 */
public class Or extends Struct {
  public Or(Term... disjunctions) {
    super(FUNCTOR_SEMICOLON, (Object[]) disjunctions);
  }

}
