package org.logic2j.engine.predicates.impl.math;

/**
 * Successor value.
 */
public class Succ extends Pred2 {

  public Succ(Object n0, Object n1) {
    super("succ", n0, n1);
  }


  // TODO: some predicates may generate 0 or multiple images
  protected Object image(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Integer) {
      return ((Integer) value) + 1;
    }
    if (value instanceof Long) {
      return ((Long) value) + 1L;
    }
    if (value instanceof Double) {
      return ((Double) value) + 1.0;
    }
    if (value instanceof Float) {
      return ((Float) value) + 1.0f;
    }
    throw new IllegalArgumentException("Forward method for " + this + " cannot handle argument " + value + " of " + value.getClass());
  }

  // TODO: some predicates may generate 0 or multiple preimates (eg. Abs(X, Y))
  protected Object preimage(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Integer) {
      return ((Integer) value) - 1;
    }
    if (value instanceof Long) {
      return ((Long) value) - 1L;
    }
    if (value instanceof Double) {
      return ((Double) value) - 1.0;
    }
    if (value instanceof Float) {
      return ((Float) value) - 1.0f;
    }
    throw new IllegalArgumentException("Reverse method for " + this + " cannot handle argument " + value + " of " + value.getClass());
  }


}
