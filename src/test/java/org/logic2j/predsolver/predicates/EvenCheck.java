package org.logic2j.predsolver.predicates;

import org.logic2j.predsolver.predicates.impl.TestingFOPredicate1;

/**
 * Does not provide any solution.
 */
public class EvenCheck extends TestingFOPredicate1<Number> {
  public EvenCheck(Number t1) {
    super("evenCheck", t1, nbr -> nbr.longValue() % 2 == 0);
  }
}
