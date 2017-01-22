package org.logic2j.predsolver.predicates.internal;

import org.logic2j.predsolver.model.Struct;
import org.logic2j.predsolver.model.Term;

/**
 * Does not provide any solution.
 */
public class Or extends Struct {
  public Or(Term... disjunctions) {
    super(FUNCTOR_SEMICOLON, (Object[])disjunctions);
  }

}
