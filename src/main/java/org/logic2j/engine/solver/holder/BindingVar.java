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

import org.logic2j.engine.model.Var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
  private BindingVar(Class<T> theType, CharSequence theName) {
    this(theType, theName, (Iterable) null);
  }

  /**
   * A "bound" {@link BindingVar} used to inject values.
   *
   * @param theType
   * @param theName
   * @param input
   */
  private BindingVar(Class<T> theType, CharSequence theName, Iterable<T> input) {
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
  private BindingVar(Class<T> theType, CharSequence theName, T... input) {
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

  private static BindingVar<String> strBVar(CharSequence theName, Stream<String> stream) {
    return new BindingVar<>(String.class, theName, stream.collect(Collectors.toList()));
  }

  public static BindingVar<Integer> intBVar(CharSequence theName) {
    return new BindingVar<>(Integer.class, theName);
  }

  public boolean isBound() {
    return input != null;
  }

  public List<T> toList() {
    if (this.input instanceof Collection) {
      return (List<T>) this.input;
    }
    final ArrayList<T> list = new ArrayList<>();
    this.input.forEach(list::add);
    return list;
  }


  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(super.toString());
    if (input == null) {
      sb.append("(empty)");
    } else {
      final Collection<T> copy = toList();
      sb.append("#").append(copy.size()).append(copy);
    }
    return sb.toString();
  }

}
