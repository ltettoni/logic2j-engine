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
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Constant;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.TermApi;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.Var.anon;
import static org.logic2j.engine.solver.Continuation.CONTINUE;

/**
 * First-Order logic Predicate, not to be confused with java.function.Predicates.
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
   * @param theFunctor
   * @param argList
   */
  public FOPredicate(String theFunctor, Object... argList) {
    super(theFunctor, createBindings(argList));
    setPredicateLogic(this::predicateLogic);
  }

  private static Object[] createBindings(Object... argList) {
    return Arrays.stream(argList).map(FOPredicate::createBinding).toArray(Object[]::new);
  }

  private static Object createBinding(Object arg) {
    if (arg==null) {
      return anon();
    }
    // Here we will convert basic types to their SimpleBindings
    if (arg instanceof Long) {
      return bind((Long)arg);
    }
    if (arg instanceof Integer) {
      return bind((Integer)arg);
    }
    if (arg instanceof String) {
      return bind((String)arg);
    }
    if (arg instanceof Double) {
      return bind((Double)arg);
    }
    if (arg instanceof Float) {
      return bind((Float)arg);
    }
    return arg;
  }


  // ---------------------------------------------------------------------------
  // The logic of this predicate
  // ---------------------------------------------------------------------------

  /**
   * This method will specify the {@link Struct#setPredicateLogic(Function)} .
   *
   * @param currentVars
   * @return The continuation, one of {@link org.logic2j.engine.solver.Continuation} values.
   */
  public abstract Integer predicateLogic(UnifyContext currentVars);


  // --------------------------------------------------------------------------
  // Bind variables and send solutions forward
  // --------------------------------------------------------------------------

  /**
   * Notify listener that a solution has been found.
   *
   * @return The {@link Continuation} as returned by listener's {@link SolutionListener#onSolution(UnifyContext)}
   */
  protected Integer notifySolution(UnifyContext currentVars) {
    final Integer continuation = currentVars.getSolutionListener().onSolution(currentVars);
    return continuation;
  }


  protected Integer notifySolutionIf(boolean condition, UnifyContext currentVars) {
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
  protected Integer unifyAndNotify(UnifyContext currentVars, Object t1, Object t2) {
    final UnifyContext afterUnification = currentVars.unify(t1, t2);

    final boolean couldUnifySomething = afterUnification != null;
    return notifySolutionIf(couldUnifySomething, afterUnification);
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
  protected Integer unifyAndNotifyMany(UnifyContext currentVars, Object t1, Iterator iter) {
    while (iter.hasNext()) {
      final Integer continuation = unifyAndNotify(currentVars, t1, iter.next());
      if (continuation != CONTINUE) {
        return continuation;
      }
    }
    return CONTINUE;
  }

  protected Integer unifyAndNotifyMany(UnifyContext currentVars, Object t1, Object[] values) {
    for (final Object value: values) {
      final Integer continuation = unifyAndNotify(currentVars, t1, value);
      if (continuation != CONTINUE) {
        return continuation;
      }
    }
    return CONTINUE;
  }

  protected <T> Integer unifyAndNotifyMany(UnifyContext currentVars, T constant, Binding<T> binding) {
    final Object reified = currentVars.reify(binding);
    if (isFreeVar(reified)) {
      return unifyAndNotify(currentVars, constant, reified);
    }
    if (isConstant(reified)) {
      for (Object value : constants(reified)) {
        final Integer continuation = unifyAndNotify(currentVars, constant, value);
        if (continuation != CONTINUE) {
          return continuation;
        }
      }
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
   * @param nameOfPrimitive Non functional - only to report the name of the primitive in case an Exception is thrown
   * @param indexOfArg      zero-based index of argument causing error
   * @throws InvalidTermException
   */
  protected void ensureBindingIsNotAFreeVar(Object term, int indexOfArg) {
    if (isFreeVar(term)) {
      // TODO Should be a kind of InvalidGoalException instead?
      final int positionOfArgument = indexOfArg + 1;
      final String nameOfPrimitive = getName();
      throw new InvalidTermException(
          "Cannot invoke primitive \"" + nameOfPrimitive + "\" with a free variable, check argument #" + positionOfArgument);
    }
  }

  // Should really provide a Stream to handle larger sets
  protected static <Q> Q[] constants(Object reified) {
    if (reified == null || isFreeVar(reified)) {
      return (Q[]) EMPTY_ARRAY;
    }
    if (reified instanceof Constant<?>) {
      // FIXME This is not good when we have streams or iterators :-( We load them in memory
      return ((Constant<Q>) reified).toArray();
    }
    // Other object: will be a scalar
    return (Q[]) new Object[] {reified};
  }

  /**
   * @param reified Result of {@link UnifyContext#reify(Object)}
   * @return true if reified is not a {@link Var}, including true when reified is null
   */
  protected static boolean isConstant(Object reified) {
    return !TermApi.isFreeVar(reified) && reified != anon();
  }

  protected static boolean isFreeVar(Object reified) {
    return TermApi.isFreeVar(reified);
  }
}
