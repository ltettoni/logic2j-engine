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

package org.logic2j.engine.predicates.impl.math.function;

import java.util.function.Function;

public interface NumericRelation extends Function<Number, Number[]> {
  Integer[] onInteger(Integer arg);

  Long[] onLong(Long arg);

  Float[] onFloat(Float arg);

  Double[] onDouble(Double arg);


  default Number[] apply(Number arg) {
    if (arg == null) {
      return null;
    }
    if (arg instanceof Integer) {
      return onInteger((Integer) arg);
    }
    if (arg instanceof Long) {
      return onLong((Long) arg);
    }
    if (arg instanceof Double) {
      return onDouble((Double) arg);
    }
    if (arg instanceof Float) {
      return onFloat((Float) arg);
    }
    throw new IllegalArgumentException(
        "Apply method for " + this.getClass().getSimpleName() + " cannot handle argument " + arg + " of " + arg.getClass());
  }
}
