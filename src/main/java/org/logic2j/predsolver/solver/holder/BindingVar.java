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

package org.logic2j.predsolver.solver.holder;

import org.logic2j.predsolver.model.Var;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link Var} with bound values to a Java objects.
 */
public class BindingVar<T> extends Var<T> {

  private Collection<T> coll = null;

  /**
   * A "free" {@link BindingVar} used only to retrieve results.
   *
   * @param theType
   * @param theName
   */
  public BindingVar(Class<T> theType, CharSequence theName) {
    this(theType, theName, null);
  }

  /**
   * A "bound" {@link BindingVar} used to inject values.
   *
   * @param theType
   * @param theName
   * @param coll
   */
  public BindingVar(Class<T> theType, CharSequence theName, Collection<T> coll) {
    super(theType, theName);
    this.coll = coll;
  }

  public static BindingVar<Integer> intBVar(CharSequence theName, Collection<Integer> coll) {
    return new BindingVar<>(Integer.class, theName, coll);
  }

  public static BindingVar<Integer> intBVar(CharSequence theName, Stream<Integer> stream) {
    return new BindingVar<>(Integer.class, theName, stream.collect(Collectors.toList()));
  }

  public static BindingVar<Integer> intBVar(CharSequence theName) {
    return new BindingVar<>(Integer.class, theName);
  }


  public boolean isFree() {
    return coll == null;
  }

  public boolean isBound() {
    return coll != null;
  }

  /**
   * Supply values
   *
   * @return The values or null when none.
   */
  public Iterator<T> iterator() {
    return coll == null ? null : coll.iterator();
  }

  void setResults(Collection<T> results) {
    coll = results;
  }

  void addResult(T result) {
    if (coll == null) {
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
    if (coll == null) {
      sb.append("(empty)");
    } else {
      sb.append("#" + coll.size());
      sb.append(coll);
    }
    return sb.toString();
  }
}
