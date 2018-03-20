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

package org.logic2j.engine.solver.holder;

import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link Var} with bound values to a Java objects.
 */
public class BindingVar<T> extends Var<T> {

  private Iterable<T> input = null;
  private List<T> result;

  /**
   * A "free" {@link BindingVar} used only to retrieve results.
   *
   * @param theType
   * @param theName
   */
  public BindingVar(Class<T> theType, CharSequence theName) {
    this(theType, theName, (Iterable) null);
  }

  /**
   * A "bound" {@link BindingVar} used to inject values.
   *
   * @param theType
   * @param theName
   * @param input
   */
  public BindingVar(Class<T> theType, CharSequence theName, Iterable<T> input) {
    super(theType, theName);
    this.input = input;
  }

  /**
   * A "bound" {@link BindingVar} used to inject values.
   *
   * @param theType
   * @param theName
   * @param input
   */
  @SafeVarargs
  public BindingVar(Class<T> theType, CharSequence theName, T... input) {
    super(theType, theName);
    this.input = Arrays.asList(input);
  }

  public static BindingVar<Integer> intBVar(CharSequence theName, Iterable<Integer> iterable) {
    return new BindingVar<>(Integer.class, theName, iterable);
  }

  public static BindingVar<Integer> intBVar(CharSequence theName, Integer... values) {
    return new BindingVar<>(Integer.class, theName, values);
  }

  public static BindingVar<Integer> intBVar(CharSequence theName, Stream<Integer> stream) {
    return new BindingVar<>(Integer.class, theName, stream.collect(Collectors.toList()));
  }

  public static BindingVar<String> strBVar(CharSequence theName) {
    return new BindingVar<>(String.class, theName);
  }


  public static BindingVar<String> strBVar(CharSequence theName, Iterable<String> iterable) {
    return new BindingVar<>(String.class, theName, iterable);
  }

  public static BindingVar<String> strBVar(CharSequence theName, String... values) {
    return new BindingVar<>(String.class, theName, values);
  }

  public static BindingVar<String> strBVar(CharSequence theName, Stream<String> stream) {
    return new BindingVar<>(String.class, theName, stream.collect(Collectors.toList()));
  }

  public static BindingVar<Integer> intBVar(CharSequence theName) {
    return new BindingVar<>(Integer.class, theName);
  }

  public boolean isFree() {
    return input == null;
  }

  public boolean isBound() {
    return input != null;
  }

  public Iterable<T> iterable() {
    return input;
  }

  /**
   * Supply values
   *
   * @return The values or null when none.
   */
  public Iterator<T> iterator() {
    return input == null ? null : input.iterator();
  }

  public List<T> toList() {
    if (this.input instanceof Collection) {
      return (List<T>) this.input;
    }
    final ArrayList<T> list = new ArrayList<>();
    this.input.forEach(list::add);
    return list;
  }

  public Set<T> toSet() {
    if (this.input instanceof Set) {
      return (Set<T>) this.input;
    }
    final HashSet<T> set = new HashSet<>();
    this.input.forEach(set::add);
    return set;
  }

  public void addResult(Object value) {
    if (result == null) {
      result = new ArrayList<>();
    }
    if (!(result instanceof Collection)) {
      throw new SolverException("BindingVar needs a Collection in order to collect solver results");
    }
    ((Collection) result).add(value);
  }

  public List<T> getResults() {
    return this.result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(super.toString());
    if (input == null) {
      sb.append("(empty)");
    } else {
      final Collection<T> copy = toList();
      sb.append("#" + copy.size());
      sb.append(copy);
    }
    return sb.toString();
  }

}
