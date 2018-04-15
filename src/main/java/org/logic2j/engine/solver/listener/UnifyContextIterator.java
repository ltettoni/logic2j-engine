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

package org.logic2j.engine.solver.listener;

import org.logic2j.engine.model.Var;
import org.logic2j.engine.unify.UnifyContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * An {@link Iterator} providing the results of attempt to unify a {@link Var}iable to a collection of values, in sequence.
 */
public class UnifyContextIterator implements Iterator<UnifyContext> {

  private final UnifyContext currentVars;
  private final Var var;
  private final Collection<?> values; // Just for reporting in toString()
  private final Iterator<?> valueIterator;

  /**
   * Instantiate for a single {@link Var}iable and a collection of values.
   *
   * @param currentVars The current state of variables
   * @param theVar      The variable to attempt unification
   * @param values      The collection of values to unify in sequence
   */
  public UnifyContextIterator(UnifyContext currentVars, Var theVar, Collection<?> values) {
    this.currentVars = currentVars;
    this.var = theVar;
    this.values = values;
    this.valueIterator = values.iterator();
  }

  /**
   * Create a new {@link UnifyContextIterator} from two existing, both must have the same variable,
   * and the result will iterate on the UNION set of the left-hand-side and the right-hand-side arguments, in respected order.
   *
   * @param currentVars
   * @param multiLHS
   * @param multiRHS
   */
  public UnifyContextIterator(UnifyContext currentVars, Iterator<UnifyContext> multiLHS, Iterator<UnifyContext> multiRHS) {
    if (!(multiLHS instanceof UnifyContextIterator)) {
      throw new UnsupportedOperationException("Left argument must be instanceof UnifyContextIterator, was of " + multiLHS.getClass());
    }
    if (!(multiRHS instanceof UnifyContextIterator)) {
      throw new UnsupportedOperationException("Right argument must be instanceof UnifyContextIterator, was of " + multiLHS.getClass());
    }
    final UnifyContextIterator left = (UnifyContextIterator) multiLHS;
    final UnifyContextIterator right = (UnifyContextIterator) multiRHS;
    if (left.var != right.var) {
      throw new UnsupportedOperationException("Must have same var to combine");
    }
    this.currentVars = currentVars;
    this.var = left.var;
    // Compose the UNION set of the LHS and RHS, in same order
    this.values = new ArrayList<>(left.values);
    this.values.retainAll(right.values);
    this.valueIterator = this.values.iterator();
  }


  /**
   * @return true if there is a next value to attempt to unify
   */
  @Override
  public boolean hasNext() {
    return valueIterator.hasNext();
  }

  /**
   * @return The new {@link UnifyContext} resulting from unifying the variable with the next value
   */
  @Override
  public UnifyContext next() {
    final Object next = this.valueIterator.next();
    return currentVars.unify(this.var, next);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Cannot remove item from this iterator");
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + this.values;
  }

}
