package org.logic2j.predsolver.predicates;


import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.impl.FOPredicate;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.solver.listener.multi.UnifyContextIterator;
import org.logic2j.predsolver.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate digits
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