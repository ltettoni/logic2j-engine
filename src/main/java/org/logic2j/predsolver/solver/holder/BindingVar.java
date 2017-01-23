package org.logic2j.predsolver.solver.holder;

import org.logic2j.predsolver.model.Var;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link Var} with bound values to a Java objects.
 */
public class BindingVar<T> extends Var<T> {

  private Collection<T> coll;

  public BindingVar(CharSequence theName) {
    this(theName, null);
  }

  public BindingVar(CharSequence theName, Collection<T> coll) {
    super(theName);
    this.coll = coll;
  }

  public boolean isBound() {
    return coll!=null;
  }

  /**
   * Supply values
   * @return The values or null when none.
   */
  public Iterator<T> iterator() {
    return coll == null ? null : coll.iterator();
  }

  void setResults(Collection<T> results) {
    coll = results;
  }

  void addResult(T result) {
    if (coll==null) {
      coll = new ArrayList<T>();
    }
    coll.add(result);
  }


  public Collection<T> toList() {
    return new ArrayList<T>(coll);
  }

  public Set<T> toSet() {
    return new HashSet<T>(coll);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(super.toString());
    if (coll==null) {
      sb.append("(empty)");
    } else {
      sb.append("#" + coll.size());
      sb.append(coll);
    }
    return sb.toString();
  }
}
