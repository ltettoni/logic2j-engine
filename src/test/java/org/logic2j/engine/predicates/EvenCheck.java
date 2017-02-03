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

package org.logic2j.engine.predicates;

import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.SimpleBinding;
import org.logic2j.engine.predicates.impl.math.Pred1;

/**
 * Check a {@link Number} is even using a Java {@link java.util.function.Predicate}.
 */
public class EvenCheck extends Pred1<Number> {
  public EvenCheck(Binding<Number> v0) {
    super("evenCheck", v0);
    this.setTest(val -> val.doubleValue() % 2 == 0);
  }

  public EvenCheck(Number v0) {
    this(SimpleBinding.cst(v0));
  }
}
