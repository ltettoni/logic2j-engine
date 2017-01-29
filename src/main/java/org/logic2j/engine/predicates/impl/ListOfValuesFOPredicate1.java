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


import org.logic2j.engine.model.Var;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

/**
 * Generate list of values in order.
 */
public abstract class ListOfValuesFOPredicate1<T> extends FOPredicate {

  private final T[] possibleValues;

  public ListOfValuesFOPredicate1(String functorName, T term, T... possibleValues) {
    super(functorName, term);
    this.possibleValues = possibleValues;
  }

  public ListOfValuesFOPredicate1(String functorName, Var<T> term, T... possibleValues) {
    super(functorName, term);
    this.possibleValues = possibleValues;
  }

  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object term = getArg(0);

    Integer continuation = Continuation.CONTINUE;
    for (T value : possibleValues) {
      continuation = unifyAndNotify(theListener, currentVars, term, value);
      if (continuation == Continuation.USER_ABORT) {
        return continuation;
      }
    }
    return continuation;
  }
}