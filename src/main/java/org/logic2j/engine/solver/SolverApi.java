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
import org.logic2j.engine.model.TermApi;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.java.Supply;
import org.logic2j.engine.solver.holder.BindingVar;
import org.logic2j.engine.solver.holder.GoalHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.logic2j.engine.predicates.Predicates.and;

/**
 * Higher level API.
 */
public class SolverApi extends Solver {
  public GoalHolder solve(Term... goals) {
    final List<Term> goalList = Arrays.stream(goals).collect(Collectors.toList());

    final Var<?>[] vars = TermApi.distinctVars(and(goalList.toArray(new Term[goalList.size()])));
    final BindingVar[] bindingVars = Arrays.stream(vars).filter(BindingVar.class::isInstance).map(BindingVar.class::cast).toArray(BindingVar[]::new);
    final BindingVar[] boundBindingVars = Arrays.stream(bindingVars).filter(bv -> bv.isBound()).toArray(BindingVar[]::new);

//    for (final BindingVar bv : bindingVars) {
//      if (bv.iterator() != null) {
//        goalList.add(0, new Supply(bv.iterator(), bv));
//      }
//    }

    if (boundBindingVars.length>0) {
      goalList.add(0, new Supply(boundBindingVars));
    }

    final Term effective = goalList.size() == 1 ? goalList.get(0) : and(goalList.toArray(new Term[goalList.size()]));
    final Object normalized = TermApi.normalize(effective);
    final BindingVar[] freeBindingVars = Arrays.stream(bindingVars).filter(bv -> bv.isFree()).toArray(BindingVar[]::new);
    return new GoalHolder(this, normalized, bindingVars);
  }
}
