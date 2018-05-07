/*
 * logic2j - "Bring Logic to your Java" - Copyright (c) 2018 Laurent.Tettoni@gmail.com
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
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.solver.Solver;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

import java.util.ArrayList;
import java.util.List;

import static org.logic2j.engine.solver.Continuation.CONTINUE;

public class FindAll<T> extends FOPredicate {


  private final Term what;
  private final Binding<List<T>> solutions;

  public FindAll(Term what, Term goal, Binding<List<T>> solutions) {
    super("findAll", what, goal, solutions);
    this.what = what;
    this.solutions = solutions;
  }

  @Override
  public int predicateLogic(UnifyContext currentVars) {
    final List results = new ArrayList();
    final SolutionListener listSolutions = currentVars1 -> {
      final Object reify = currentVars1.reify(what);
      results.add(reify);
      return CONTINUE;
    };
    final Solver solver = currentVars.getSolver();
    solver.solveGoal(getArg(1), currentVars.withListener(listSolutions));

    return currentVars.unifyAndNotify(results, this.solutions);
  }
}
