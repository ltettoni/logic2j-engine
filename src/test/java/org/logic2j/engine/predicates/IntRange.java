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


import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.solver.listener.UnifyContextIterator;
import org.logic2j.engine.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * IntRange(min, middle, max) is true when min <= middle < max.
 */
public class IntRange extends FOPredicate {
  private static final Logger logger = LoggerFactory.getLogger(IntRange.class);

  public IntRange(Object min, final Object middle, Object max) {
    super("intRange", min, middle, max);
  }


  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object minBound = currentVars.reify(getArg(0));
    final Object iterating = currentVars.reify(getArg(1));
    final Object maxBound = currentVars.reify(getArg(2));

    ensureBindingIsNotAFreeVar(minBound, "int_range_classic/3", 0);
    ensureBindingIsNotAFreeVar(maxBound, "int_range_classic/3", 2);

    final int min = ((Number) minBound).intValue();
    final int max = ((Number) maxBound).intValue();

    if (iterating instanceof Var) {
      final List<Integer> values = new ArrayList<Integer>();
      for (int val = min; val < max; val++) {
        values.add(val);
      }

      final UnifyContextIterator unifyContextIterator = new UnifyContextIterator(currentVars, (Var) iterating, values);
      logger.info("{} is going to notify multi solutions: {}", this, values);
      return theListener.onSolutions(unifyContextIterator);
    } else {
      // Check
      final int iter = ((Number) iterating).intValue();

      return notifySolutionIf(min <= iter && iter < max, theListener, currentVars);

    }
  }

}