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

import static org.logic2j.engine.model.SimpleBindings.bind;

import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.SimpleBindings;
import org.logic2j.engine.model.Struct;


/**
 * Less or Equal Than comparison.
 */
public class LE<T extends Comparable<T>> extends Comp2<T> {

  public LE(Binding<T> arg0, Binding<T> arg1) {
    super("le", arg0, arg1);
    setCheck((v0, v1) -> v0.compareTo(v1) <= 0);
  }

  public LE(Binding<T> arg0, T arg1) {
    this(arg0, bind(arg1));
  }

  public LE(T arg0, Binding<T> arg1) {
    this(bind(arg0), arg1);
  }

  public static LE valueOf(Struct struct) {
    if (struct.getPredicateSignature().equals("=</2")) {
      return new LE(SimpleBindings.newBinding(struct.getArg(0)),
              SimpleBindings.newBinding(struct.getArg(1)));
    }
    return null;
  }

  @Override
  public String sqlOperator() {
    return "<=";
  }
}
