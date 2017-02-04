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
    setImage(t -> (T)forward.apply(t));
    setPreimages(t -> (T[])reverse.apply(t));
  }



  private static NumericFunction forward = new NumericFunction() {
    @Override
    public Integer onInteger(Integer arg) {
      return Math.abs(arg);
    }

    @Override
    public Long onLong(Long arg) {
      return Math.abs(arg);
    }

    @Override
    public Float onFloat(Float arg) {
      return Math.abs(arg);
    }

    @Override
    public Double onDouble(Double arg) {
      return Math.abs(arg);
    }
  };


  private static NumericRelation reverse = new NumericRelation() {
    @Override
    public Integer[] onInteger(Integer arg) {
      final int v = Math.abs(arg);
      if (arg < 0) {
        return new Integer[0];
      }
      return v == 0 ? new Integer[] {v} : new Integer[] {-v, v};
    }

    @Override
    public Long[] onLong(Long arg) {
      final long v = Math.abs(arg);
      if (arg < 0) {
        return new Long[0];
      }
      return v == 0 ? new Long[] {v} : new Long[] {-v, v};
    }

    @Override
    public Float[] onFloat(Float arg) {
      final float v = Math.abs(arg);
      if (arg < 0) {
        return new Float[0];
      }
      return v == 0 ? new Float[] {v} : new Float[] {-v, v};
    }

    @Override
    public Double[] onDouble(Double arg) {
      final double v = Math.abs(arg);
      if (arg < 0) {
        return new Double[0];
      }
      return v == 0 ? new Double[] {v} : new Double[] {-v, v};
    }
  };


}
