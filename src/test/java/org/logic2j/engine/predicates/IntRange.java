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
import org.logic2j.engine.model.Constant;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.solver.Continuation.CONTINUE;

/**
 * IntRange(min, middle, max) is true when min <= middle < max.
 */
public class IntRange extends FOPredicate {
  private static final Logger logger = LoggerFactory.getLogger(IntRange.class);

  public IntRange(Binding<Integer> min, final Binding<Integer> middle, Binding<Integer> max) {
    super("intRange", min, middle, max);
  }

  public IntRange(Integer min, final Binding<Integer> middle, Integer max) {
    this(bind(min), middle, bind(max));
  }


  @Override
  public int predicateLogic(UnifyContext currentVars) {
    final Object minBound = currentVars.reify(getArg(0));
    final Object iterating = currentVars.reify(getArg(1));
    final Object maxBound = currentVars.reify(getArg(2));

    ensureBindingIsNotAFreeVar(minBound, 0);
    ensureBindingIsNotAFreeVar(maxBound, 2);

    final int min = ((Constant<Integer>) minBound).toScalar();
    final int max = ((Constant<Integer>) maxBound).toScalar();

    if (isFreeVar(iterating)) {
      final List<Integer> values = IntStream.range(min, max).boxed().collect(Collectors.toList());

      logger.info("{} is going to notify solutions: {}", this, values);
      for (int increment = min; increment < max; increment++) {
        final int cont = unifyAndNotify(currentVars, iterating, increment);
        if (cont != CONTINUE) {
          return cont;
        }
      }
    } else {
      // Check: notify one solution for any binding within range
      for (Object val : (Iterable<Object>) stream(iterating)::iterator) {
        if (val instanceof Number) {
          final int v = ((Number) val).intValue();
          final int cont = notifySolutionIf(min <= v && v < max, currentVars);
          if (cont != CONTINUE) {
            return cont;
          }
        }
      }
    }
    return CONTINUE;
  }
}