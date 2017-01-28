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
package org.logic2j.predsolver.predicates;

import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.impl.Eq;
import org.logic2j.predsolver.predicates.impl.Fail;
import org.logic2j.predsolver.predicates.impl.True;
import org.logic2j.predsolver.predicates.impl.firstorder.Exists;
import org.logic2j.predsolver.predicates.impl.firstorder.Not;
import org.logic2j.predsolver.predicates.impl.java.FOFilter;
import org.logic2j.predsolver.predicates.impl.java.FOMap;
import org.logic2j.predsolver.predicates.internal.And;
import org.logic2j.predsolver.predicates.internal.Cut;
import org.logic2j.predsolver.predicates.internal.Or;
import org.logic2j.predsolver.solver.Solver;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Factory methods for common predicates.
 */
public final class Predicates {

  public static final True ttrue = new True();
  public static final Fail fail = new Fail();
  public static final Cut cut = new Cut();

  public static <T> Var<T> _() {
    return Var.anonymous();
  }

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

  public static Exists exists(Solver solver, Term term) {
    return new Exists(solver, term);
  }

  public static Not not(Solver solver, Term term) {
    return new Not(solver, term);
  }

  public static Eq eq(Object t1, Object t2) {
    return new Eq(t1, t2);
  }

  public static <T> FOFilter filter(Var<T> var, Predicate<T> pred) {
    return new FOFilter<>(var, pred);
  }

  public static <T, R> FOMap map(Var<T> v1, Function<T, R> javaFunction, Var<R> v2) {
    return new FOMap<>(v1, javaFunction, v2);
  }


}
