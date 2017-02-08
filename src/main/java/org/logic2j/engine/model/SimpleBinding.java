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
import java.util.*;
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
  Class<T> type;

  private long size = -1; // <0 means unknown or not enumerable
  private T[] data; // Data stored there
  private Set<T> cachedSet = null; // Data optionally stored there (if contains operations are requested)

  private Stream<T> stream = null;
  private Iterator<T> iterator = null;

  /**
   * Use static factories instead.
   *
   * @param type
   */
  private SimpleBinding(Class<T> type, T[] values, Stream<T> stream, Iterator<T> iterator) {
    this.type = type;
    this.data = values;
    this.stream = stream;
    this.iterator = iterator;
    this.size = this.data != null ? this.data.length : -1;
  }

  // ---------------------------------------------------------------------------
  // Defining values
  // ---------------------------------------------------------------------------

  public static <T> SimpleBinding<T> empty(Class<T> type) {
    return new SimpleBinding<T>(type, (T[]) new Object[0], null, null);
  }

  public static <T> SimpleBinding<T> bind(T... values) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Empty SimpleBinding array, cannot determine data type of instances.");
    }
    return new SimpleBinding(values[0].getClass(), values, null, null);
  }

  public static <T> SimpleBinding<T> bind(Collection<T> coll) {
    if (coll.size() == 0) {
      throw new IllegalArgumentException("Empty SimpleBinding collection, cannot determine data type of instances.");
    }
    return new SimpleBinding(coll.iterator().next().getClass(), coll.toArray(), null, null);
  }

  /**
   * Consume the stream immediately and only once, cache all data in this object.
   *
   * @param stream
   * @param <T>
   * @return
   */
  public static <T> SimpleBinding<T> bind(Stream<T> stream) {
    return new SimpleBinding(null, null, stream, null);
  }

  /**
   * Consume the iterator immediately and only once, cache all data in this object.
   *
   * @param iterator
   * @param <T>
   * @return
   */
  public static <T> SimpleBinding<T> bind(Iterator<T> iterator) {
    return new SimpleBinding(null, null, null, iterator);
  }


  // ---------------------------------------------------------------------------
  // De-streaming or de-iterating values into data if requested so
  // ---------------------------------------------------------------------------

  private void ensureData() {
    if (size >= 0) {
      return;
    }
    Stream<T> effectiveStream = stream;
    if (iterator != null) {
      // Collect stream to array (this will consume the stream only once)
      final List<T> collector = new ArrayList<>();
      iterator.forEachRemaining(collector::add);
      effectiveStream = collector.stream();
    }
    if (effectiveStream != null) {
      // Collect stream to array (this will consume the stream only once)
      final Object[] asObjects = effectiveStream.toArray(Object[]::new);
      if (asObjects.length == 0) {
        throw new IllegalArgumentException("Empty SimpleBinding stream, cannot determine data type of instances.");
      }
      final Class<T> elementClass = (Class<T>) asObjects[0].getClass();
      this.type = elementClass;
      this.data = Arrays.stream(asObjects).toArray(n -> (T[]) Array.newInstance(elementClass, n));
      this.size = this.data.length;
    }
  }

  /**
   * Cache the data currently held in a Set (for efficient test), and then use the set for testing presence.
   *
   * @param value
   * @return true if value is contained in the data.
   */
  public boolean contains(T value) {
    ensureData();
    if (this.size <= 0) {
      return false;
    }
    if (this.cachedSet == null) {
      synchronized (this) {
        if (this.cachedSet == null) {
          this.cachedSet = Arrays.stream(this.data).collect(Collectors.toSet());
        }
      }
    }
    return this.cachedSet.contains(value);
  }

  // ---------------------------------------------------------------------------
  // Consuming values
  // ---------------------------------------------------------------------------

  public long size() {
    ensureData();
    return this.size;
  }

  public T[] toArray() {
    ensureData();
    return this.data;
  }

  public Stream<T> toStream() {
    return Arrays.stream(this.data);
  }

  @Override
  public Class<T> getType() {
    ensureData();
    return type;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(this.type.getSimpleName());
    sb.append('s');
    if (this.data != null) {
      sb.append(Arrays.stream(data).map(Object::toString).collect(Collectors.joining(",", "<", ">")));
    } else {
      sb.append("no-data-yet");
    }
    return sb.toString();
  }

}
