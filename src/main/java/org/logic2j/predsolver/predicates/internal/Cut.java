package org.logic2j.predsolver.predicates.internal;

import org.logic2j.predsolver.model.Struct;

/**
 * Provides one solution and cuts backtracking.
 */
public class Cut extends Struct {
  public Cut() {
    super(FUNCTOR_CUT);
  }

}
