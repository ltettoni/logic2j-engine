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

import static java.util.Collections.singletonList;
import static org.logic2j.engine.model.SimpleBindings.newBinding;
import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.solver.Continuation.CONTINUE;

import java.util.Arrays;
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
   *
   * @param arguments
   * @return See {@link #newBindingOrStruct(Object)}
   */
  public static Object[] newBindingsOrStructs(Object... arguments) {
    return Arrays.stream(arguments).map(FOPredicate::newBindingOrStruct).toArray(Object[]::new);
  }

  /**
   * Make any object as a good argument to a {@link Struct} for binding values
   *
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


  /**
   * Will notify a solution to the {@link org.logic2j.engine.solver.Solver} if condition is true.
   *
   * @param condition
   * @param currentVars
   * @return The result of {@link #notifySolution(UnifyContext)} if condition is true, otherwise CONTINUE.
   */
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
    if (t1 instanceof Constant) {
      final Constant<?> c1 = (Constant<?>) t1;
      for (Object e1 : c1.toList()) {
        final int continuation = unifyAndNotify(currentVars, e1, t2);
        if (continuation != CONTINUE) {
          return continuation;
        }
      }
      return CONTINUE;
    }
    if (t2 instanceof Constant) {
      final Constant<?> c2 = (Constant<?>) t2;
      for (Object e2 : c2.toList()) {
        final int continuation = unifyAndNotify(currentVars, t1, e2);
        if (continuation != CONTINUE) {
          return continuation;
        }
      }
      return CONTINUE;
    }
    // Normal scalar unification
    final UnifyContext afterUnification = currentVars.unify(t1, t2);
    final boolean didUnify = afterUnification != null;
    return notifySolutionIf(didUnify, afterUnification);
  }


  // --------------------------------------------------------------------------
  // Support methods to help writing predicates
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


  protected Integer toInt(Object value) {
    return toTypedValue(value, Integer.class);
  }

  protected Long toLong(Object value) {
    return toTypedValue(value, Long.class);
  }

  protected Float toFloat(Object value) {
    return toTypedValue(value, Float.class);
  }

  protected Double toDouble(Object value) {
    return toTypedValue(value, Double.class);
  }

  protected String toString(Object value) {
    final CharSequence cs = toTypedValue(value, CharSequence.class);
    return cs != null ? cs.toString() : null;
  }

  protected <Q> Q toTypedValue(Object value, Class<Q> type) {
    assert value != null : "Value of binding cannot be null";
    assert type != null : "Expected type of binding must be specified";
    if (isFreeVar(value)) {
      return null;
    }
    if (value instanceof Constant) {
      final Constant constant = (Constant) value;
      return (Q) toTypedValue(constant.toScalar(), type);
    }
    final boolean castable = type.isAssignableFrom(value.getClass());
    if (!castable) {
      if (type == Long.class) {
        return (Q) new Long(value.toString());
      }
      if (type == Integer.class) {
        return (Q) new Integer(value.toString());
      }
      if (type == Float.class) {
        return (Q) new Float(value.toString());
      }
      if (type == Double.class) {
        return (Q) new Double(value.toString());
      }
      if (type == String.class) {
        return (Q) value.toString();
      }
      throw new InvalidTermException("Term of " + value.getClass() + " not allowed where expecting " + type + "; value was " + value);
    }
    // Do the cast
    return (Q) value;
  }


  protected <Q> Iterable<Q> toIterable(Object value) {
    if (isFreeVar(value)) {
      return null;
    }
    if (value instanceof Constant) {
      final Constant constant = (Constant) value;
      return constant.toList();
    }
    return singletonList((Q) value);
  }

  /**
   * @param reified Result of {@link UnifyContext#reify(Object)}
   * @return true if reified is not a free {@link Var}, including true when reified is null
   */
  protected static boolean isConstant(Object reified) {
    return !termApi().isFreeVar(reified)/* && reified != anon() */;
  }

  protected static boolean isFreeVar(Object reified) {
    return termApi().isFreeVar(reified);
  }

}
