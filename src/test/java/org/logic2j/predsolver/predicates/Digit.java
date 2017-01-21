package org.logic2j.predsolver.predicates;


import org.logic2j.predsolver.predicates.impl.ListOfValuesPredicate1;

/**
 * Generate digits
 */
public class Digit extends ListOfValuesPredicate1 {

  public Digit(Object term) {
    super("digit", term, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  }

}