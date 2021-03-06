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

package org.logic2j.engine.predicates.impl.generator;


import org.logic2j.engine.model.Binding;
import org.logic2j.engine.predicates.impl.Pred1Generator;

import static org.logic2j.engine.model.SimpleBindings.bind;

/**
 * Generate digits
 */
public class Even extends Pred1Generator<Integer> {

  public Even(Binding<Integer> term) {
    super("even", term, 0, 2, 4, 6, 8);
  }


  public Even(Integer v0) {
    this(bind(v0));
  }
}