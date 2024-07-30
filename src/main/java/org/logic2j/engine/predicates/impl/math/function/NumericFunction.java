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

/**
 * A {@link java.util.function.Function} that delegates its apply{@link Function#apply(Object)}
 * to dedicated implementations for Integer, Long, Float and Double.
 */
public interface NumericFunction extends Function<Number, Number> {
  Integer onInteger(Integer arg);

  Long onLong(Long arg);

  Float onFloat(Float arg);

  Double onDouble(Double arg);

  default Number apply(Number arg) {
      return switch (arg) {
          case null -> null;
          case Integer i -> onInteger(i);
          case Long l -> onLong(l);
          case Double v -> onDouble(v);
          case Float v -> onFloat(v);
          default -> throw new IllegalArgumentException(
                  "Apply method for " + this.getClass().getSimpleName() + " cannot handle argument " + arg + " of " + arg.getClass());
      };
  }
}
