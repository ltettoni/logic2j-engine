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


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides data to predicates: one or several values of a given type. Can provide values from running {@link Iterator}s or {@link Stream}s.
 * Only one method: {@link #empty(Class)} allows empty content. Other methods require at least one element to determine the data type.
 * TODO: one should infer the data type by scanning all elements, not only checking on the first.
 *
 * @param <T>
 */
public class SimpleBinding<T> implements Binding<T> {
  private final Class<T> type;
  private long size = -1; // <0 means unknown or not enumerable
  private final T[] values; // Data stored there
  private Set<T> cachedSet = null; // Data optionally stored there (if contains operations are requested)

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

  /**
   * Consume the stream immediately and only once, cache all data in this object.
   *
   * @param stream
   * @param <T>
   * @return
   */
  public static <T> SimpleBinding<T> bind(Stream<T> stream) {
    // Collect stream to array (this will consume the stream only once)
    final Object[] asObjects = stream.toArray(Object[]::new);
    if (asObjects.length == 0) {
      throw new IllegalArgumentException("Empty SimpleBinding stream, cannot determine data type of instances.");
    }
    final Class<?> elementClass = asObjects[0].getClass();
    final T[] data = Arrays.stream(asObjects).toArray(n -> (T[]) Array.newInstance(elementClass, n));
    return bind(data);
  }

  /**
   * Consume the iterator immediately and only once, cache all data in this object.
   *
   * @param iterator
   * @param <T>
   * @return
   */
  public static <T> SimpleBinding<T> bind(Iterator<T> iterator) {
    // Collect stream to array (this will consume the stream only once)
    final List<T> collector = new ArrayList<>();
    iterator.forEachRemaining(collector::add);
    return bind(collector);
  }


  public long size() {
    return this.size;
  }

  public T[] toArray() {
    return this.values;
  }

  public Stream<T> toStream() {
    return Arrays.stream(this.values);
  }

  /**
   * Cache the data currently held in a Set (for efficient test), and then use the set for testing presence.
   *
   * @param value
   * @return true if value is contained in the data.
   */
  public boolean contains(T value) {
    if (this.size <= 0) {
      return false;
    }
    if (this.cachedSet == null) {
      synchronized (this) {
        if (this.cachedSet == null) {
          this.cachedSet = Arrays.stream(this.values).collect(Collectors.toSet());
        }
      }
    }
    return this.cachedSet.contains(value);
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
