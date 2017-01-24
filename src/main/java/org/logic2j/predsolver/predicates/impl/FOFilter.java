package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.model.Var;

import java.util.function.Predicate;

/**
 * Test a {@link Var} using a Java predicate, only provides a solution of the variable is bound.
 */
public class FOFilter<T> extends FOPredicateJavaPredicate {
  public FOFilter(Var<T> term, Predicate<T> javaPredicate) {
    super("_javaLambdaPredicate", term, javaPredicate);
  }
}
