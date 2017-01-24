package org.logic2j.predsolver.predicates;

import org.logic2j.predsolver.predicates.impl.FOPredicateJavaPredicate;

/**
 * Check an Integer is even.
 */
public class EvenCheck extends FOPredicateJavaPredicate<Number> {
  public EvenCheck(Number t1) {
    super("evenCheck", t1, nbr -> nbr.longValue() % 2 == 0);
  }
}
