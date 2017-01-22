package org.logic2j.predsolver.predicates.internal;

import org.logic2j.predsolver.model.Struct;

/**
 * Provides one solution and cuts backtracking.
 * The implementation is hard-coded in the Solver, hence we do not provided it here.
 */
public class Cut extends Struct {
  public Cut() {
    super(FUNCTOR_CUT);
  }

}
