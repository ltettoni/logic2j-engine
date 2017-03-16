/*
 * logic2j - "Bring Logic to your Java" - Copyright (c) 2017 Laurent.Tettoni@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.logic2j.engine.predicates;

import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.Eq;
import org.logic2j.engine.predicates.impl.Fail;
import org.logic2j.engine.predicates.impl.True;
import org.logic2j.engine.predicates.impl.firstorder.Count;
import org.logic2j.engine.predicates.impl.firstorder.Exists;
import org.logic2j.engine.predicates.impl.firstorder.Not;
import org.logic2j.engine.predicates.impl.math.Pred1Tester;
import org.logic2j.engine.predicates.impl.math.Pred2;
import org.logic2j.engine.predicates.internal.And;
import org.logic2j.engine.predicates.internal.Cut;
import org.logic2j.engine.predicates.internal.Or;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.logic2j.engine.model.SimpleBindings.bind;

/**
 * Factory methods for common predicates.
 */
public final class Predicates {

  public static final True ttrue = new True(); // Singleton
  public static final Fail fail = new Fail(); // Singleton
  public static final Cut cut = new Cut(); // Singleton

  public static Var anon() {
    return Var.anon();
  }

//  public static Var<Integer> anonInt() {
//    return Var.anon();
//  }
//
//  public static Var<String> anonStr() {
//    return Var.anon();
//  }

  public static And and(Term... conjunctions) {
    return new And(conjunctions);
  }

  public static Term conjunction(Term... conjunctions) {
    if (conjunctions.length == 0) {
      return ttrue;
    }
    if (conjunctions.length == 1) {
      return conjunctions[0];
    }
    return new And(conjunctions);
  }

  public static Or or(Term... disjunctions) {
    return new Or(disjunctions);
  }

  public static Term disjunction(Term... disjunctions) {
    if (disjunctions.length == 0) {
      return ttrue;
    }
    if (disjunctions.length == 1) {
      return disjunctions[0];
    }
    return new Or(disjunctions);
  }

  public static Exists exists(Term term) {
    return new Exists(term);
  }

  public static Exists exists(Term term, Binding<Boolean> result) {
    return new Exists(term, result);
  }

  public static Count count(Term term, Binding<Long> number) {
    return new Count(term, number);
  }

  public static Count count(Term term, Long number) {
    return new Count(term, bind(number));
  }

  public static Count count(Term term, Integer number) {
    return new Count(term, bind((long)number));
  }

  public static Not not(Term term) {
    return new Not(term);
  }

  public static  <T> Eq eq(Binding<T> t1, Binding<T> t2) {
    return new Eq(t1, t2);
  }

  public static Eq eq(Binding<Term> t1, Term t2) {
    return new Eq(t1, t2);
  }

  public static <T> Pred1Tester filter(Var<T> var, Predicate<T> pred) {
    return new Pred1Tester<T>("_lambdaFilter", var).withTest(pred);
  }

  public static <T, R> Pred2 map(Var<T> v1, Function<T, R> javaFunction, Var<R> v2) {
    return new Pred2<T, R>("_lambdaMap", v1, v2).withImage(javaFunction);
  }

}
