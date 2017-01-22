package org.logic2j.predsolver.predicates;

import org.logic2j.predsolver.predicates.impl.TestingFOPredicate1;

/**
 * Does not provide any solution.
 */
public class EvenCheck extends TestingFOPredicate1<Number> {
  public EvenCheck(Number t1) {
    super("evenCheck", t1);
  }


  @Override
  public boolean test(Number number) {
    final long value = number.longValue();
    return value % 2 == 0;
  }
}
