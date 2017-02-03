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

/**
 * Successor value.
 */
public class Succ extends Pred2 {

  public Succ(Object n0, Object n1) {
    super("succ", n0, n1);
  }


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
