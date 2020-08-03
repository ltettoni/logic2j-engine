package org.logic2j.engine.predicates;

import static org.logic2j.engine.solver.Continuation.CONTINUE;

import org.logic2j.engine.exception.InvalidTermException;
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Constant;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.unify.UnifyContext;

public class Difference<T extends Number> extends FOPredicate {


  private Binding<T> lower;
  private Binding<T> delta;
  private Binding<T> upper;

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

  protected Integer toInt(Object value) {
    return toTypedValue(value, Integer.class);
  }

  protected <T> T toTypedValue(Object value, Class<T> type) {
    assert value != null : "Value of binding cannot be null";
    assert type != null : "Expected type of binding must be specified";
    if (isFreeVar(value)) {
      return null;
    }
    if (value instanceof Constant) {
      final Constant constant = (Constant) value;
      return (T) toTypedValue(constant.toScalar(), constant.getType());
    }
    if (!type.isAssignableFrom(value.getClass())) {
      throw new InvalidTermException("Term of " + value.getClass() + " not allowed where expecting " + type + "; value was " + value);
    }
    return (T) value;
  }

}
