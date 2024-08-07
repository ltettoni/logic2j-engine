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

import org.logic2j.engine.model.Var;
import org.logic2j.engine.solver.listener.CountingSolutionListener;
import org.logic2j.engine.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.logic2j.engine.model.TermApiLocator.termApi;

/**
 * Used in test cases to extract number of solutions and solutions to a goal.
 */
class ExtractingSolutionListener extends CountingSolutionListener {
  private static final Logger logger = LoggerFactory.getLogger(ExtractingSolutionListener.class);

  private final Object goal;
  private final Var<?>[] vars;
  private final Set<String> varNames;
  private final List<Map<String, Object>> solutions;

  public ExtractingSolutionListener(Object theGoal) {
    this.goal = theGoal;
    this.vars = termApi().distinctVars(this.goal);
    // Here we use an expensive TreeSet but this is only for test cases - it will get the solutions ordered and will help assertions
    this.varNames = new TreeSet<>();
    for (final Var<?> var : vars) {
      this.varNames.add(var.getName());
    }
    this.varNames.add(Var.WHOLE_SOLUTION_VAR_NAME); // This pseudo var means the whole solution

    this.solutions = new ArrayList<>();

    logger.debug("Init listener for \"{}\"", theGoal);
  }

  @Override
  public int onSolution(UnifyContext currentVars) {
    final Object solution = currentVars.reify(goal);
    logger.debug(" solution: {}", solution);

    final Map<String, Object> solutionVars = new HashMap<>();
    solutionVars.put(Var.WHOLE_SOLUTION_VAR_NAME, solution); // The global solution
    for (final Var<?> var : vars) {
      final Object varValue = currentVars.reify(var);
      solutionVars.put(var.getName(), varValue);
    }
    this.solutions.add(solutionVars);

    return super.onSolution(currentVars);
  }

  public void report() {
      switch (count()) {
          case 0 -> logger.debug("Solving \"{}\" yields no solution", goal);
          case 1 -> logger.debug("Solving \"{}\" yields a single solution", goal);
          default -> logger.debug("Solving \"{}\" yields {} solution(s)", goal, count());
      }
  }

  public Collection<Var<?>> getVariables() {
    return Arrays.asList(this.vars);
  }

  public List<Map<String, Object>> getSolutions() {
    return this.solutions;
  }

  public List<Object> getValues(String varName) {
    if (!varNames.contains(varName)) {
      throw new IllegalArgumentException("Variable \"" + varName + "\" not defined in goal \"" + this.goal + '"');
    }
    final List<Object> values = new ArrayList<>(this.solutions.size());
    for (final Map<String, Object> solution : this.solutions) {
      values.add(solution.get(varName));
    }
    return values;
  }


  public Collection<String> getVarNames() {
    return this.varNames;
  }
}
