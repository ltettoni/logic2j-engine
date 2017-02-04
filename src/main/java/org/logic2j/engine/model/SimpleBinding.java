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


import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Provides predicates one or several values of a given type.
 *
 * @param <T>
 */
public class SimpleBinding<T> implements Binding<T> {
  private final Class<T> type;
  private long size = -1; // <0 means unknown or not enumerable
  private final T[] values;

  /**
   * Use static factories instead.
   *
   * @param type
   */
  private SimpleBinding(Class<T> type, T... values) {
    this.type = type;
    this.values = values;
    this.size = this.values.length;
  }

  public static <T> SimpleBinding<T> empty(Class<T> type) {
    return new SimpleBinding<T>(type);
  }

  public static <T> SimpleBinding<T> bind(T... values) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Empty SimpleBinding array, cannot determine data type of instances.");
    }
    return new SimpleBinding(values[0].getClass(), values);
  }

  public static <T> SimpleBinding<T> bind(Collection<T> coll) {
    if (coll.size() == 0) {
      throw new IllegalArgumentException("Empty SimpleBinding collection, cannot determine data type of instances.");
    }
    return new SimpleBinding(coll.iterator().next().getClass(), coll.toArray());
  }


  public long size() {
    return this.size;
  }

  public T[] getArray() {
    return this.values;
  }

  @Override
  public Class<T> getType() {
    return type;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(this.type.getSimpleName());
    sb.append('s');
    sb.append(Arrays.stream(values).map(Object::toString).collect(Collectors.joining(",", "<", ">")));
    return sb.toString();
  }
}
