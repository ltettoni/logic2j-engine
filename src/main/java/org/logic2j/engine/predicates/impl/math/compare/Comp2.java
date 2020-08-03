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

package org.logic2j.engine.predicates.impl.math.compare;

import static org.logic2j.engine.solver.Continuation.CONTINUE;

import java.util.function.BiFunction;
import org.logic2j.engine.exception.InvalidTermException;
import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.external.RDBComparisonPredicate;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.unify.UnifyContext;

/**
 * 2-arguments comparison.
 */
public abstract class Comp2<T> extends FOPredicate implements RDBComparisonPredicate {

  private BiFunction<T, T, Boolean> check = (v0, v1) -> {
    throw new UnsupportedOperationException("Function \"check()\" of " + Comp2.this + " was not defined");
  };

  /**
   * A binary predicate with two functions defining the forward and reverse mappings.
   *
   * @param theFunctor
   */
  protected Comp2(String theFunctor, Binding<T> arg0, Binding<T> arg1) {
    super(theFunctor, arg0, arg1);
  }

  @Override
  public final int predicateLogic(UnifyContext currentVars) {
    final Object n0 = currentVars.reify(getArg(0));
    final Object n1 = currentVars.reify(getArg(1));

    ensureBindingIsNotAFreeVar(n0, 0);
    ensureBindingIsNotAFreeVar(n1, 1);

    return comparison(currentVars, n0, n1);
  }

  protected int comparison(UnifyContext currentVars, Object n0, Object n1) {
    if (isConstant(n0)) {
      if (isConstant(n1)) {
        final T[] values1 = FOPredicate.<T>stream(n1).toArray(n -> (T[]) new Object[n]);
        for (T c0 : FOPredicate.<T>list(n0)) {
          for (final T c1 : values1) {
            // Both bound values - check
            final int continuation = notifySolutionIf(check.apply(c0, c1), currentVars);
            if (continuation != CONTINUE) {
              return continuation;
            }
          }
        }
        return CONTINUE;
      } else {
        throw new SolverException("Should not be here we require bound vars");
      }
    }

    throw new SolverException("Should not be here we require bound vars");
  }

  // --------------------------------------------------------------------------
  // Fluent setters
  // --------------------------------------------------------------------------

  public Comp2<T> withCheck(BiFunction<T, T, Boolean> check) {
    setCheck(check);
    return this;
  }


  // --------------------------------------------------------------------------
  // Accessors
  // --------------------------------------------------------------------------


  public BiFunction<T, T, Boolean> getCheck() {
    return check;
  }

  public void setCheck(BiFunction<T, T, Boolean> check) {
    this.check = check;
  }
}
