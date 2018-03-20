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

package org.logic2j.engine.predicates.impl.math.compare;

import org.logic2j.engine.model.Binding;

import static org.logic2j.engine.model.SimpleBindings.bind;



/**
 * Less Than comparison.
 */
public class LT<T extends Number> extends Comp2<T> {
  /**
   * A binary predicate with two functions defining the forward and reverse mappings.
   *
   * @param arg0
   * @param arg1
   */
  public LT(Binding<T> arg0, Binding<T> arg1) {
    super("lt", arg0, arg1);
    setCheck((v0, v1) -> v0.doubleValue() < v1.doubleValue());
  }

  public LT(Binding<T> arg0, T arg1) {
    this(arg0, bind(arg1));
  }

  public LT(T arg0, Binding<T> arg1) {
    this(bind(arg0), arg1);
  }

  @Override
  public String sqlOperator() {
    //    return "{0}<{1}";
    return "<";
  }
}
