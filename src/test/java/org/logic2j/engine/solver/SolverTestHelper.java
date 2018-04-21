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

package org.logic2j.engine.solver;

import org.logic2j.engine.model.Term;
import org.logic2j.engine.solver.holder.GoalHolder;

import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.predicates.Predicates.and;

/**
 * Higher level convenience API only used in test cases.
 */
public class SolverTestHelper extends Solver {
  public GoalHolder solve(Term... goals) {
    final Term effective = goals.length == 1 ? goals[0] : and(goals);
    final Object normalized = termApi().normalize(effective);
    return new GoalHolder(this, normalized, null);
  }
}
