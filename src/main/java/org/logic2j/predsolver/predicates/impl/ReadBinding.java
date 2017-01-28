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

package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Read from Java values into one {@link Var}iable (bound or not).
 */
public class ReadBinding<T> extends FOPredicate {
  private static final Logger logger = LoggerFactory.getLogger(ReadBinding.class);

  private final Iterator<T> iterator;

  public ReadBinding(Iterator<T> javaValues, Var<T> var) {
    super("supply", var);
    this.iterator = javaValues;
  }


  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object var = getArg(0);
    final Object reified = currentVars.reify(var);
    if (reified instanceof Var) {
      // Still a free var, we will attempt to read values from the getter and provide bindings

      if (iterator != null) {
        return unifyAndNotifyMany(theListener, currentVars, (Var) reified, iterator);
      }
      return Continuation.CONTINUE;
    } else {
      // Variable is bound to a value

      if (iterator != null) {
        final Set<Object> set = new HashSet<>();
        while (iterator.hasNext()) {
          set.add(iterator.next());
        }
        return notifySolutionIf(set.contains(reified), theListener, currentVars);
      } else {
        logger.warn("Cannot store instant value {}", reified);
        return Continuation.CONTINUE;
      }
    }
  }
}
