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

package org.logic2j.engine.predicates.impl.firstorder;

import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.predicates.external.RDBCompatiblePredicate;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.solver.Solver;
import org.logic2j.engine.solver.listener.CountingSolutionListener;
import org.logic2j.engine.unify.UnifyContext;

/**
 * Succeeds if the specified goal provides a single solution (and none other is sought); fails if
 * the specified goal does not provide a single one.
 */
public class Count extends FOPredicate implements RDBCompatiblePredicate {

  public Count(Term theGoal, Binding<Integer> nbSolutions) {
    super("count", theGoal, nbSolutions);
  }

  @Override
  public int predicateLogic(UnifyContext currentVars) {

    // Solve against a minimal SolutionListener just interested on the first solution
    final CountingSolutionListener counter = new CountingSolutionListener();
    final Solver solver = currentVars.getSolver();
    solver.solveGoal(getArg(0), currentVars.withListener(counter));

    final int nbr = counter.count();
    return unifyAndNotify(currentVars, nbr, getArg(1));
  }


}
