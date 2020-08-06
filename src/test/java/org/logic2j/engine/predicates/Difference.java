package org.logic2j.engine.predicates;

import static org.logic2j.engine.solver.Continuation.CONTINUE;

import org.logic2j.engine.model.Binding;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.unify.UnifyContext;

public class Difference<T extends Number> extends FOPredicate {


  protected Difference(Binding<T> lower, Binding<T> delta, Binding<T> upper) {
    super("difference", lower, delta, upper);
  }

  @Override
  public int predicateLogic(UnifyContext currentVars) {
    final Integer low = toInt(currentVars.reify(getArg(0)));
    final Integer del = toInt(currentVars.reify(getArg(1)));
    final Integer up = toInt(currentVars.reify(getArg(2)));

    // Different logic depending on which is the free variable
    if (low!=null && del!=null) {
      return unifyAndNotify(currentVars, low + del, getArg(2));
    }
    if (del!=null && up!=null) {
      return unifyAndNotify(currentVars, up - del, getArg(0));
    }
    if (low!=null && up!=null) {
      return unifyAndNotify(currentVars, up - low, getArg(1));
    }
    // No solution
    return CONTINUE;
  }

}
