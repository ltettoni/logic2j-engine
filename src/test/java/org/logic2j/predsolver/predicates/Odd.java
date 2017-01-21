package org.logic2j.predsolver.predicates;


import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.impl.ListOfValuesPredicate1;

/**
 * Generate digits
 */
public class Odd extends ListOfValuesPredicate1<Integer> {

  public Odd(Integer term) {
    super("odd", term, 1, 3, 5, 7, 9);
  }


  public Odd(Var<Integer> term) {
    super("odd", term, 0, 2, 4, 6, 8);
  }

}