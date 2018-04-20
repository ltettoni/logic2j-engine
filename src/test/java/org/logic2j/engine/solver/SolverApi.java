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
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.Supply;
import org.logic2j.engine.solver.holder.BindingVar;
import org.logic2j.engine.solver.holder.GoalHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.predicates.Predicates.and;

/**
 * Higher level API only used in test cases.
 */
public class SolverApi extends Solver {
  public GoalHolder solve(Term... goals) {
    final Var[] vars = termApi().distinctVars(and(goals));
    final BindingVar[] bindingVars = Arrays.stream(vars).filter(BindingVar.class::isInstance).map(BindingVar.class::cast).toArray(BindingVar[]::new);
    final BindingVar[] boundBindingVars = Arrays.stream(bindingVars).filter(BindingVar::isBound).toArray(BindingVar[]::new);

    // Compose goals
    final List<Term> allGoals = new ArrayList<>();
    if (boundBindingVars.length > 0) {
      allGoals.add(0, new Supply(boundBindingVars));
    }
    for (Term goal: goals) {
      allGoals.add(goal);
    }

    final Term effective = allGoals.size() == 1 ? allGoals.get(0) : and(allGoals.toArray(new Term[0]));
    final Object normalized = termApi().normalize(effective);
    return new GoalHolder(this, normalized, null);
  }
}
