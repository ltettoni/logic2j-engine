package org.logic2j.engine.model;

/**
 * A term that may one datum or several data of a certain type.
 */
public interface Binding<T> {

  Class<T> getType();

}
