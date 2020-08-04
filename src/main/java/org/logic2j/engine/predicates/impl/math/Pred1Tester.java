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

package org.logic2j.engine.predicates.impl.math;

import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.unify.UnifyContext;

import java.util.function.Predicate;

import static java.lang.String.format;
import static org.logic2j.engine.solver.Continuation.CONTINUE;

/**
 * 1-arguments first-order predicates with a java testing (predicate) function.
 * Can only test constants or variable with value(s), cannot generate data
 * into free variables. For that use {@link org.logic2j.engine.predicates.impl.Pred1Generator}.
 */
public class Pred1Tester<T> extends FOPredicate {

  private Predicate<T> test = v -> {
    throw new UnsupportedOperationException("Predicate \"test()\" of " + Pred1Tester.this + " was not defined");
  };

  /**
   * An unary predicate with a testing function.
   *
   * @param theFunctor
   */
  public Pred1Tester(String theFunctor, Binding<T> arg0) {
    super(theFunctor, arg0);
  }


  @Override
  public final int predicateLogic(UnifyContext currentVars) {
    final Iterable<T> iter = this.toIterable(currentVars.reify(getArg(0)));

    if (iter != null) {
      for (T c0 : iter) {
        final boolean found = this.test.test(c0);
        final int continuation = notifySolutionIf(found, currentVars);
        if (continuation != CONTINUE) {
          return continuation;
        }
      }
      return CONTINUE;
    } else {
      // free variables - no solution
      return CONTINUE;
    }

  }

  // --------------------------------------------------------------------------
  // Fluent setters
  // --------------------------------------------------------------------------

  public Pred1Tester<T> withTest(Predicate<T> test) {
    this.test = test;
    return this;
  }

  // --------------------------------------------------------------------------
  // Accessors
  // --------------------------------------------------------------------------

  public Predicate<T> getTest() {
    return test;
  }

  public void setTest(Predicate<T> test) {
    this.test = test;
  }
}
