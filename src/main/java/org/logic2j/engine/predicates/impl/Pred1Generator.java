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


import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Constant;
import org.logic2j.engine.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.solver.Continuation.CONTINUE;

/**
 * Bind one value form a {@link Constant}, in sequence.
 */
public abstract class Pred1Generator<T> extends FOPredicate {

  private final Constant<T> allowedValues;

  public Pred1Generator(String functorName, Binding<T> term, Constant<T> allowedValues) {
    super(functorName, term);
    this.allowedValues = allowedValues;
  }

  @SafeVarargs
  public Pred1Generator(String functorName, Binding<T> term, T... allowedValues) {
    this(functorName, term, bind(allowedValues));
  }

  @Override
  public int predicateLogic(UnifyContext currentVars) {
    final Object reified = currentVars.reify(getArg(0));
    if (isFreeVar(reified)) {
      // Still a free var, we will attempt to read values from the getter and provide bindings

      if (allowedValues != null) {
        return unifyAndNotifyMany(currentVars, reified, allowedValues.toArray());
      }
      return CONTINUE;
    } else if (isConstant(reified)) {
      // Variable is bound to a value
      for (T val : FOPredicate.<T>list(reified)) {
        final boolean contains = allowedValues.contains(val);
        final int continuation = notifySolutionIf(contains, currentVars);
        if (continuation != CONTINUE) {
          return continuation;
        }
      }
      return CONTINUE;
    } else {
      throw new SolverException(format("Should not be handling %s in %s", reified, this));
    }
  }
}