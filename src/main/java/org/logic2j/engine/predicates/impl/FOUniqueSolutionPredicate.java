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

package org.logic2j.engine.predicates.impl;

import org.logic2j.engine.unify.UnifyContext;

/**
 * A {@link FOPredicate} that calls a side-effect method and
 * always produces a single solution (irrelevant of what it does),
 * this is typically used for I/O such as logging, etc.
 * This predicate always proceeds with the solving, there is no way to interrupt the continuation.
 * Just implement the {@link #sideEffect(UnifyContext)} method.
 */
public abstract class FOUniqueSolutionPredicate extends FOPredicate {

  public FOUniqueSolutionPredicate(String theFunctor, Object... argList) {
    super(theFunctor, argList);
  }

  // ---------------------------------------------------------------------------
  // The code to invoke
  // ---------------------------------------------------------------------------

  public abstract void sideEffect(UnifyContext currentVars);


  @Override
  public final Integer predicateLogic(UnifyContext currentVars) {
    sideEffect(currentVars);
    return notifySolution(currentVars);
  }
}
