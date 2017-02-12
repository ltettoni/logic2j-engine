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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides data to predicates: one or several values of a given type.
 * Can provide values from running {@link Iterator}s or {@link Stream}s.
 * Only one method: {@link #empty(Class)} allows empty content. Other methods require at least one element to determine the data type.
 * TODO: one should infer the data type by scanning all elements, not only checking on the first.
 *
 * @param <T>
 */
public class SimpleBindings<T> {
  private Class<T> type;

  private long size = -1; // <0 means unknown or not enumerable
  private T[] data; // Data stored there
  private Set<T> cachedSet = null; // Data optionally stored there (if contains operations are requested)

  private Stream<T> stream = null;
  private Iterator<T> iterator = null;
  private boolean consumed = false;

  /**
   * Use static factories instead.
   *
   * @param type
   */
  private SimpleBindings(Class<T> type, T[] values, Stream<T> stream, Iterator<T> iterator) {
    this.type = type;
    this.data = values;
    this.stream = stream;
    this.iterator = iterator;
    this.size = this.data != null ? this.data.length : -1;
  }

  // ---------------------------------------------------------------------------
  // Defining values
  // ---------------------------------------------------------------------------

  public static <T> Constant<T> empty(Class<T> type) {
    return new ConstantBase<T>() {

      @Override
      public boolean isSingleFeed() {
        return false;
      }

      @Override
      public long size() {
        return 0;
      }

      @Override
      public T[] toArray() {
        return (T[]) new Object[0];
      }

      @Override
      public T toScalar() {
        return null;
      }

      @Override
      public Stream<T> toStream() {
        return Stream.empty();
      }

      @Override
      public boolean contains(T value) {
        return false;
      }

      @Override
      public Class getType() {
        return type;
      }
    };
  }

  /**
   * Supply one value, must be non-null so that its type can be determined.
   * If you need to optionally have null values specify an {@link java.util.Optional}
   * @param supplier
   * @param <T>
   * @return
   */
  public static <T> Constant<T> bind(Supplier<T> supplier) {
    return new ConstantBase<T>() {
      @Override
      public boolean isSingleFeed() {
        return false;
      }

      @Override
      public long size() {
        return 1;
      }

      @Override
      public T[] toArray() {
        return (T[]) new Object[] {supplier.get()};
      }

      @Override
      public T toScalar() {
        return supplier.get();
      }

      @Override
      public Stream<T> toStream() {
        return Stream.of(supplier.get());
      }

      @Override
      public boolean contains(T value) {
        return value.equals(supplier.get());
      }

    };
  }

  public static <T> Constant<T> bind(T... values) {
    return new ConstantBase<T>() {
      @Override
      public boolean isSingleFeed() {
        return false;
      }

      @Override
      public long size() {
        return values.length;
      }

      @Override
      public T[] toArray() {
        return values;
      }

      @Override
      public T toScalar() {
        if (values.length != 1) {
          throw new IllegalStateException("Trying to get scalar from array of " + values.length + " elements: " + Arrays.asList(values));
        }
        return values[0];
      }

      @Override
      public Stream<T> toStream() {
        return Arrays.stream(values);
      }

      @Override
      public boolean contains(T value) {
        for (int i = 0; i < values.length; i++) {
          if (value.equals(values[i])) {
            return true;
          }
        }
        return false;
      }
    };
  }

  public static <T> Constant<T> bind(Collection<T> coll) {
    return new ConstantBase<T>() {
      @Override
      public boolean isSingleFeed() {
        return false;
      }

      @Override
      public long size() {
        return coll.size();
      }

      @Override
      public T[] toArray() {
        return (T[]) coll.toArray();
      }

      @Override
      public T toScalar() {
        if (coll.size() != 1) {
          throw new IllegalStateException("Trying to get scalar from collection of " + coll.size() + " elements: " + coll);
        }
        return coll.iterator().next();
      }

      @Override
      public Stream<T> toStream() {
        return coll.stream();
      }

      @Override
      public boolean contains(T value) {
        return coll.contains(value);
      }
    };
  }

  /**
   * Consume the stream immediately and only once, cache all data in this object.
   *
   * @param stream
   * @param <T>
   * @return
   */
  public static <T> Constant<T> bind(Stream<T> stream) {
    return new ConstantBase<T>() {
      private T[] data = null;

      @Override
      public boolean isSingleFeed() {
        return true;
      }

      @Override
      public long size() {
        consumeNow();
        return this.data.length;
      }

      @Override
      public T[] toArray() {
        consumeNow();
        return data;
      }

      @Override
      public T toScalar() {
        consumeNow();
        return Arrays.stream(this.data).findFirst().orElseThrow(() -> new IllegalArgumentException("Empty stream cannot provide a scalar form it"));
      }

      @Override
      public boolean contains(T value) {
        consumeNow();
        return Arrays.stream(this.data).anyMatch(value::equals);
      }

      @Override
      public Stream<T> toStream() {
        consumeNow();
        return Arrays.stream(this.data);
      }

      private void consumeNow() {
        if (this.data == null) {
          final Object[] asObjects = stream.toArray(Object[]::new);
          if (asObjects.length == 0) {
            throw new IllegalArgumentException("Empty SimpleBinding stream, cannot determine data type of instances.");
          }
          final Class<T> elementClass = (Class<T>) asObjects[0].getClass();
          this.data = Arrays.stream(asObjects).toArray(n -> (T[]) Array.newInstance(elementClass, n));
        }
      }

    };
  }

  /**
   * Consume the iterator immediately and only once, cache all data in this object.
   *
   * @param iterator
   * @param <T>
   * @return
   */
  public static <T> Constant<T> bind(Iterator<T> iterator) {
    return new ConstantBase<T>() {
      private T[] data = null;

      @Override
      public boolean isSingleFeed() {
        return true;
      }

      @Override
      public long size() {
        consumeNow();
        return this.data.length;
      }

      @Override
      public T[] toArray() {
        consumeNow();
        return data;
      }

      @Override
      public T toScalar() {
        consumeNow();
        return Arrays.stream(this.data).findFirst().orElseThrow(() -> new IllegalArgumentException("Empty stream cannot provide a scalar form it"));
      }

      @Override
      public boolean contains(T value) {
        consumeNow();
        return Arrays.stream(this.data).anyMatch(value::equals);
      }

      @Override
      public Stream<T> toStream() {
        final Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
      }

      private void consumeNow() {
        if (this.data == null) {
          final List<T> coll = new ArrayList<T>();
          iterator.forEachRemaining(val -> coll.add(val));
          final Object[] asObjects = coll.toArray();
          if (asObjects.length == 0) {
            throw new IllegalArgumentException("Empty Constant iterator, cannot determine data type of instances.");
          }
          final Class<T> elementClass = (Class<T>) asObjects[0].getClass();
          this.data = Arrays.stream(asObjects).toArray(n -> (T[]) Array.newInstance(elementClass, n));
        }
      }

    };
  }


  // ---------------------------------------------------------------------------
  // De-streaming or de-iterating values into data if requested so
  // ---------------------------------------------------------------------------
//
//  private synchronized void ensureData() {
//    if (size >= 0) {
//      return;
//    }
//    Stream<T> effectiveStream = stream;
//
//    checkConsumed();
//
//    if (iterator != null) {
//      // Collect stream to array (this will consume the stream only once)
//      final List<T> collector = new ArrayList<>();
//      iterator.forEachRemaining(collector::add);
//      effectiveStream = collector.stream();
//      this.iterator = null; // Consumed!
//    }
//    if (this.stream != null) {
//      this.stream = null;
//    }
//    if (effectiveStream != null) {
//      // Collect stream to array (this will consume the stream only once)
//      final Object[] asObjects = effectiveStream.toArray(Object[]::new);
//      if (asObjects.length == 0) {
//        throw new IllegalArgumentException("Empty SimpleBinding stream, cannot determine data type of instances.");
//      }
//      final Class<T> elementClass = (Class<T>) asObjects[0].getClass();
//      this.type = elementClass;
//      this.data = Arrays.stream(asObjects).toArray(n -> (T[]) Array.newInstance(elementClass, n));
//      this.size = this.data.length;
//    }
//    this.consumed = true;
//  }
//
//  private void checkConsumed() {
//    if (consumed) {
//      throw new IllegalStateException(this + " cannot consume a Stream or Iterator more than once !");
//    }
//  }
//
//  /**
//   * Cache the data currently held in a Set (for efficient test), and then use the set for testing presence.
//   *
//   * @param value
//   * @return true if value is contained in the data.
//   */
//  public boolean contains(T value) {
//    ensureData();
//    if (this.size <= 0) {
//      return false;
//    }
//    if (this.cachedSet == null) {
//      synchronized (this) {
//        if (this.cachedSet == null) {
//          this.cachedSet = Arrays.stream(this.data).collect(Collectors.toSet());
//        }
//      }
//    }
//    return this.cachedSet.contains(value);
//  }


  private static abstract class ConstantBase<T> implements Constant<T> {

    @Override
    public Class getType() {
      return toScalar().getClass();
    }

    public String toString() {
      final StringBuilder sb = new StringBuilder();
      if (this.getType() != null) {
        sb.append(this.getType().getSimpleName());
        sb.append('s');
      } else {
        sb.append("NoType");
      }
      if (this.size() >= 0) {
        sb.append(this.toStream().map(Object::toString).collect(Collectors.joining(",", "<", ">")));
      } else {
        sb.append("<no-data-yet>");
      }
      return sb.toString();
    }

  }
}
