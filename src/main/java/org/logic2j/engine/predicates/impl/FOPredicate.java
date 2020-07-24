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

import static org.logic2j.engine.model.SimpleBindings.newBinding;
import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.model.Var.anon;
import static org.logic2j.engine.solver.Continuation.CONTINUE;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.logic2j.engine.exception.InvalidTermException;
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Constant;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

/**
 * First-Order logic Predicate (not to be confused with java.function.Predicate).
 * This is a bridge between inference and the Java implementation of the user's logic.
 * <p/>
 * All subclasses will implement {@link #predicateLogic(UnifyContext)} which is called by the
 * inference engine to bind solutions.
 * <p/>
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
    super(functor, newBindingsOrStructs(arguments));
  }

  /**
   * Convert to valid arguments to a {@link FOPredicate}
   * @param arguments
   * @return See {@link #newBindingOrStruct(Object)}
   */
  public static Object[] newBindingsOrStructs(Object... arguments) {
    return Arrays.stream(arguments).map(FOPredicate::newBindingOrStruct).toArray(Object[]::new);
  }

  /**
   * Make any object as a good argument to a {@link Struct} for binding values
   * @param arg Any object
   * @return Usually a {@link Binding}, or a {@link Struct}.
   */
  public static Object newBindingOrStruct(Object arg) {
    if (arg instanceof Struct) {
      return arg;
    }
    return newBinding(arg);
  }

  // ---------------------------------------------------------------------------
  // The logic of this predicate
  // ---------------------------------------------------------------------------

  /**
   * Override this method with the logic of the predicate.
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

  /**
   * Unify terms t1 and t2, and if they could be unified, call theListener with the solution of the newly
   * unified variables; return the result from notifying. If not, return CONTINUE.
   *
   * @param currentVars
   * @param t1
   * @param t2
   * @return
   */
  protected int unifyAndNotify(UnifyContext currentVars, Object t1, Object t2) {
    final UnifyContext afterUnification = currentVars.unify(t1, t2);
    final boolean didUnify = afterUnification != null;
    return notifySolutionIf(didUnify, afterUnification);
  }

  /**
   * Unify terms t1 and constant values from iter, and if they could be unified, call theListener with the solution of the newly
   * unified variables; return the result from notifying. If not, return CONTINUE.
   *
   * @param currentVars
   * @param t1
   * @param iter
   * @return
   */
  protected <T> int unifyAndNotifyMany(UnifyContext currentVars, Object t1, Iterator<T> iter) {
    while (iter.hasNext()) {
      final T value = iter.next();
      final int continuation = unifyAndNotify(currentVars, t1, value);
      if (continuation != CONTINUE) {
        return continuation;
      }
    }
    return CONTINUE;
  }

  protected <T> int unifyAndNotifyMany(UnifyContext currentVars, Object t1, List<T> values) {
    for (final Object value : values) {
      final int continuation = unifyAndNotify(currentVars, t1, value);
      if (continuation != CONTINUE) {
        return continuation;
      }
    }
    return CONTINUE;
  }

  protected int unifyAndNotifyMany(UnifyContext currentVars, Object t1, Object[] values) {
    for (final Object value : values) {
      final int continuation = unifyAndNotify(currentVars, t1, value);
      if (continuation != CONTINUE) {
        return continuation;
      }
    }
    return CONTINUE;
  }

  protected <T> int unifyAndNotifyMany(UnifyContext currentVars, Object t1, Stream<T> values) {
    return unifyAndNotifyMany(currentVars, t1, values.iterator());
  }

  protected <T> int unifyAndNotifyMany(UnifyContext currentVars, T constant, Binding<T> binding) {
    final Object reified = currentVars.reify(binding);
    if (isFreeVar(reified)) {
      return unifyAndNotify(currentVars, constant, reified);
    }
    if (isConstant(reified)) {
      return unifyAndNotifyMany(currentVars, constant, list(reified));
    }
    return CONTINUE;
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
