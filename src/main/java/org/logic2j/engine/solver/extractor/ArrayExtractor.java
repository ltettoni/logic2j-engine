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

package org.logic2j.engine.solver.extractor;


import org.logic2j.engine.model.Var;
import org.logic2j.engine.unify.UnifyContext;

import static org.logic2j.engine.model.TermApiLocator.termApi;

/**
 * A {@link SolutionExtractor} that will extract values of
 * multiple variables, returned as an Array, indiced by the position specified.
 * Typically used to find all bindings of a multi-variable goal, in a very efficient way.
 */
public class ArrayExtractor implements SolutionExtractor<Object[]> {

  private final Var<?>[] vars;
  private final int nbVars;

  /**
   * Extract values of a solution for the specified variables, in positional order.
   * @param vars The variables to extract values from, in the order desired
   */
  public ArrayExtractor(Var<?>... vars) {
    int high = 0;
    this.vars = vars;
    this.nbVars = this.vars.length;
  }

  /**
   * Extract values of a solution for all the variables of goal, the order
   * is determined by TermApi#distinctVars()
   * @param goal
   */
  public ArrayExtractor(Object goal) {
    this(termApi().distinctVars(goal));
  }

  /**
   * @param currentVars
   * @return The values reified from the currentVars, in the order specified by constructor
   */
  @Override
  public Object[] extractSolution(UnifyContext currentVars) {
    final Object[] result = new Object[this.nbVars];
    for (int i = 0; i < this.nbVars; i++) {
      final Var<?> var = this.vars[i];
      final Object value = currentVars.reify(var);
      result[i] = value;
    }
    return result;
  }
}
