package org.logic2j.predsolver.model;

import java.util.Collection;
import java.util.Iterator;

/**
 * A {@link Var} with bound values to a Java objects.
 */
public class BoundVar<T> extends Var<T> {

  private final Collection<T> coll;

  public BoundVar(CharSequence theName, Collection<T> coll) {
    super(theName);
    this.coll = coll;
  }

  public Iterator<T> iterator() {
    return coll == null ? null : coll.iterator();
  }
}
