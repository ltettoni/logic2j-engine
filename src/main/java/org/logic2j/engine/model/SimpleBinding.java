package org.logic2j.engine.model;


import java.util.Arrays;
import java.util.stream.Collectors;

public class SimpleBinding<T> implements Binding<T> {
  private final Class<T> type;
  private final T[] values;

  public static <T> Binding<T> cst(T value) {
    return new SimpleBinding(value.getClass(), value);
  }

  public static <T> Binding<T> arr(T... values) {
    if (values.length==0) {
      throw new IllegalArgumentException("Empty SimpleBinding array, cannot determine data type of instances.");
    }
    return new SimpleBinding(values[0].getClass(), values);
  }

  public SimpleBinding(Class<T> type, T... values) {
    this.type = type;
    this.values = values;
  }

  public int size() {
    return values.length;
  }

  public T[] values() {
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
