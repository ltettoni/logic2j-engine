package org.logic2j.predsolver.predicates;


import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.impl.ListOfValuesFOPredicate1;

/**
 * Generate digits
 */
public class Even extends ListOfValuesFOPredicate1<Integer> {

  public Even(Integer term) {
    super("even", term, 0, 2, 4, 6, 8);
  }


  public Even(Var<Integer> term) {
    super("even", term, 0, 2, 4, 6, 8);
  }

}