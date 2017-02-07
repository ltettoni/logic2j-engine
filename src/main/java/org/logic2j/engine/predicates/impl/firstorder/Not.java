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

import org.logic2j.engine.model.Term;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.solver.listener.ExistsSolutionListener;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

/**
 * Logical negation, succeeds if the specified goal does not provide a single solution; fails if
 * the specified goal provides at least one solution (and none other is sought).
 * This actually means "Not exists".
 */
public class Not extends FOPredicate {
  public static final String FUNCTOR = "\\+";

  public Not(Term theGoal) {
    super(FUNCTOR, theGoal);
  }

  @Override
  public Integer predicateLogic(SolutionListener theListener, UnifyContext currentVars) {

    // Solve against a minimal SolutionListener just interested on the first solution
    final ExistsSolutionListener seekOnlyTheFirstSolution = new ExistsSolutionListener();
    currentVars.getSolver().solveGoal(getArg(0), seekOnlyTheFirstSolution, currentVars);

    final boolean doesNotExist = !seekOnlyTheFirstSolution.exists();
    return notifySolutionIf(doesNotExist, theListener, currentVars);
  }


}
