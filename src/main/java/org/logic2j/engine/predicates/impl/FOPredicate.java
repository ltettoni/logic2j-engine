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

package org.logic2j.engine.predicates.impl;

import org.logic2j.engine.exception.InvalidTermException;
import org.logic2j.engine.model.Constant;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.model.Var.anon;
import static org.logic2j.engine.solver.Continuation.CONTINUE;

/**
 * First-Order logic Predicate (not to be confused with java.function.Predicate).
 * First-order logic is about binding variables to all solutions, not just checking one value.
 * <p>
 * All subclasses are required to implement {@link #predicateLogic(UnifyContext)}.
 * <p>
 * Support methods are provided to check the {@link Var}iables received from the {@link UnifyContext},
 * and unify variables to values, or check if unification of terms is possible, and then
 * send solutions to the {@link SolutionListener}.
 */
public abstract class FOPredicate extends Struct {
  protected static final Object[] EMPTY_ARRAY = new Object[0];

  /**
   * A functional predicate is a plain data structure like a {@link Struct}, with some executable logic
   * attached through the {@link #predicateLogic(UnifyContext)} abstract method.
   *
   * @param functor
   * @param arguments
   */
  protected FOPredicate(String functor, Object... arguments) {
    super(functor, arguments);
  }

  // ---------------------------------------------------------------------------
  // The logic of this predicate
  // ---------------------------------------------------------------------------

  /**
   * This method will implement the logic of the predicate.
   *
   * @param currentVars
   * @return The continuation, one of {@link org.logic2j.engine.solver.Continuation} values.
   */
  public abstract int predicateLogic(UnifyContext currentVars);


  // --------------------------------------------------------------------------
  // Bind variables and send solutions forward
  // --------------------------------------------------------------------------

  /**
   * Notify listener that a solution has been found.
   *
   * @return The {@link Continuation} as returned by listener's {@link SolutionListener#onSolution(UnifyContext)}
   */
  protected int notifySolution(UnifyContext currentVars) {
    return currentVars.getSolutionListener().onSolution(currentVars);
  }


  protected int notifySolutionIf(boolean condition, UnifyContext currentVars) {
    if (condition) {
      return notifySolution(currentVars);
    } else {
      return CONTINUE;
    }
  }


  // --------------------------------------------------------------------------
  // Support methods to
  // --------------------------------------------------------------------------

  /**
   * Make sure term is not a free {@link Var}.
   *
   * @param term
   * @param indexOfArg zero-based index of argument causing error
   * @throws InvalidTermException
   */
  protected void ensureBindingIsNotAFreeVar(Object term, int indexOfArg) {
    if (isFreeVar(term)) {
      final int positionOfArgument = indexOfArg + 1;
      final String nameOfPrimitive = getName();
      throw new InvalidTermException(
          "Cannot invoke primitive \"" + nameOfPrimitive + "\" with a free variable, check argument #" + positionOfArgument);
    }
  }


  protected static <Q> List<Q> list(Object reified) {
    if (reified == null || isFreeVar(reified)) {
      return Collections.emptyList();
    }
    if (reified instanceof Constant<?>) {
      return ((Constant<Q>) reified).toList();
    }
    // Other object: will be a scalar
    return Collections.singletonList((Q) reified);
  }


  protected static <Q> Stream<Q> stream(Object reified) {
    if (reified == null || isFreeVar(reified)) {
      return Stream.empty();
    }
    if (reified instanceof Constant<?>) {
      return ((Constant<Q>) reified).toStream();
    }
    // Other object: will be a scalar
    return Stream.of((Q) reified);
  }

  /**
   * @param reified Result of {@link UnifyContext#reify(Object)}
   * @return true if reified is not a free {@link Var}, including true when reified is null
   */
  protected static boolean isConstant(Object reified) {
    return !termApi().isFreeVar(reified) && reified != anon();
  }

  protected static boolean isFreeVar(Object reified) {
    return termApi().isFreeVar(reified);
  }

}
