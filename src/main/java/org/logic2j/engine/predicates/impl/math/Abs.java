package org.logic2j.engine.predicates.impl.math;

/**
 * Absolute value. Interesting because this is not a bijection.
 */
public class Abs extends Pred2 {

  public Abs(Object n0, Object n1) {
    super("abs", n0, n1);
  }

  /**
   * Preimage is easy, abs() is a function.
   *
   * @param value
   * @return single value
   */
  protected Object image(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Integer) {
      return Math.abs((Integer) value);
    }
    if (value instanceof Long) {
      return Math.abs((Long) value);
    }
    if (value instanceof Double) {
      return Math.abs((Double) value);
    }
    if (value instanceof Float) {
      return Math.abs((Float) value);
    }
    throw new IllegalArgumentException("Forward method for " + this + " cannot handle argument " + value + " of " + value.getClass());
  }

  @Override
  protected Object preimage(Object value) {
    // Not called
    return null;
  }

  /**
   * Two-value preimage
   *
   * @param value
   * @return Zero, one, or two preimages.
   */
  protected Object[] preimages(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Integer) {
      final Integer arg = (Integer) value;
      final int v = Math.abs(arg);
      if (arg < 0) {
        return new Integer[0];
      }
      return v == 0 ? new Integer[] {v} : new Integer[] {-v, v};
    }
    if (value instanceof Long) {
      final Long arg = (Long) value;
      final long v = Math.abs(arg);
      if (arg < 0) {
        return new Long[0];
      }
      return v == 0 ? new Long[] {v} : new Long[] {-v, v};
    }
    if (value instanceof Double) {
      final Double arg = (Double) value;
      final double v = Math.abs(arg);
      if (arg < 0) {
        return new Double[0];
      }
      return v == 0 ? new Double[] {v} : new Double[] {-v, v};
    }
    if (value instanceof Float) {
      final Float arg = (Float) value;
      final float v = Math.abs(arg);
      if (arg < 0) {
        return new Float[0];
      }
      return v == 0 ? new Float[] {v} : new Float[] {-v, v};
    }
    throw new IllegalArgumentException("Reverse method for " + this + " cannot handle argument " + value + " of " + value.getClass());
  }


}
