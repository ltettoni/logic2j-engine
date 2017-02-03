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

package org.logic2j.engine.predicates.impl.math;

import org.logic2j.engine.model.Binding;

/**
 * Absolute value. Interesting because this is not a bijection.
 */
public class Abs<T extends Number> extends Pred2<T, T> {

  public Abs(Binding<T> n0, Binding<T> n1) {
    super("succ", n0, n1);
    setImage(t -> (T)forward(t));
    setPreimages(t -> (T[])reverse(t));
  }


  /**
   * Preimage is easy, abs() is a function.
   *
   * @param value
   * @return single value
   */
  protected static Number forward(Number value) {
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
    throw new IllegalArgumentException("Forward method for " + Abs.class.getSimpleName() + " cannot handle argument " + value + " of " + value.getClass());
  }


  /**
   * Two-value preimage
   *
   * @param value
   * @return Zero, one, or two preimages.
   */
  protected static Number[] reverse(Number value) {
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
    throw new IllegalArgumentException("Reverse method for " + Abs.class.getSimpleName() + " cannot handle argument " + value + " of " + value.getClass());
  }


}
