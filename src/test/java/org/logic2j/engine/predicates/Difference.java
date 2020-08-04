package org.logic2j.engine.predicates;

import static org.logic2j.engine.solver.Continuation.CONTINUE;

import org.logic2j.engine.model.Binding;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.unify.UnifyContext;

public class Difference<T extends Number> extends FOPredicate {


  private final Binding<T> lower;
  private final Binding<T> delta;
  private final Binding<T> upper;

  protected Difference(Binding<T> lower, Binding<T> delta, Binding<T> upper) {
    super("difference", lower, delta, upper);
    this.lower = lower;
    this.delta = delta;
    this.upper = upper;
  }

  @Override
  public int predicateLogic(UnifyContext currentVars) {
    final Integer low = toInt(currentVars.reify(lower));
    final Integer del = toInt(currentVars.reify(delta));
    final Integer up = toInt(currentVars.reify(upper));

    // Different logic depending on which is the free variable
    if (low!=null && del!=null) {
      return unifyAndNotify(currentVars, low + del, upper);
    }
    if (del!=null && up!=null) {
      return unifyAndNotify(currentVars, up - del, lower);
    }
    if (low!=null && up!=null) {
      return unifyAndNotify(currentVars, up - low, delta);
    }
    // No solution
    return CONTINUE;
  }

}
