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

package org.logic2j.engine.model;

import java.util.stream.Stream;

/**
 * A {@link Binding} that provides constant value (scalar) or values (vectorial).
 */
public interface Constant<T> extends Binding {

  /**
   * @return true if data comes from a stream that cannot be consumed more than once.
   */
  boolean isSingleFeed();

  /**
   * Calculate the size. In case of a stream this consumes it.
   * @return Cardinality of data: 0=empty, 1=scalar, >1=vectorial, -1=unknown
   */
  long size();

  /**
   * In case of a stream this consumes it.
   * @return
   */
  T[] toArray();

  /**
   * In case of a stream this consumes it.
   * @return
   */
  T toScalar();

  Stream<T> toStream();

  /**
   * In case of a stream this consumes it.
   * @param value
   * @return
   */
  boolean contains(T value);

}
