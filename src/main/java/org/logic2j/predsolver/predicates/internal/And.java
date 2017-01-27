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

package org.logic2j.predsolver.predicates.internal;

import org.logic2j.predsolver.model.Struct;
import org.logic2j.predsolver.model.Term;

/**
 * Logical AND.
 * The implementation is hard-coded in the Solver, hence we do not provided it here.
 */
public class And extends Struct {
  public And(Term... conjunctions) {
    super(FUNCTOR_COMMA, (Object[]) conjunctions);
  }

}
