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

package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.solver.Solver;
import org.logic2j.predsolver.solver.listener.ExistsSolutionListener;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Unification operator "=".
 */
public class Not extends FOPredicate {
  public static final String FUNCTOR = "\\+";
  private Solver solver;

  public Not(Solver solver, Term theGoal) {
    super(FUNCTOR, theGoal);
    this.solver = solver;
  }

  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {

    // Solve against a minimal SolutionListener just interested on the first solution
    final ExistsSolutionListener goalListener = new ExistsSolutionListener();
    this.solver.solveGoal(getArg(0), goalListener, currentVars);

    final boolean doesNotExist = !goalListener.exists();
    return notifySolutionIf(doesNotExist, theListener, currentVars);
  }


}
