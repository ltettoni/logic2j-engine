package org.logic2j.engine.model;


public class SimpleBinding<T> implements Binding<T> {
  private final Class<T> type;
  private final T[] values;

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
}
