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

package org.logic2j.engine.solver.holder;

import org.logic2j.engine.model.Var;
import org.logic2j.engine.solver.Solver;
import org.logic2j.engine.solver.listener.CountingSolutionListener;
import org.logic2j.engine.solver.listener.ExistsSolutionListener;
import org.logic2j.engine.unify.UnifyContext;

import java.util.Map;

/**
 * An intermediate class in the fluent API to extract solutions; a GoalHolder holds the state of what the user
 * wishes to calculate, being either the existence of, the number of solutions, or individual or multiple values
 * of one particular variable or of all vars.
 * This object will launch the solver only for methods exists() or count(). For other
 * methods it just returns instances of SolutionHolder which further delay the execution.
 */
public class GoalHolder {

  private final Solver solver;
  private final Object goal;
  private final BindingVar[] bindingVars;

  public GoalHolder(Solver solver, Object theGoal, BindingVar[] bindingVars) {
    this.solver = solver;
    this.goal = theGoal;
    this.bindingVars = bindingVars;
  }

  public boolean exists() {
    final ExistsSolutionListener listener = new ExistsSolutionListener();
    solver.solveGoal(goal, listener);
    return listener.exists();
  }

  public boolean none() {
    return !exists();
  }

  /**
   * TODO should rather be based on limit() with an iteration up to solution #2
   *
   * @return true if only one solution to goal.
   */
  public boolean unique() {
    // TODO This is not an efficient implementation
    return count() == 1;
  }

  /**
   * TODO should rather be based on limit() with an iteration up to solution #2
   *
   * @return true if there is more than one solution.
   */
  public boolean multiple() {
    // TODO This is not an efficient implementation
    return count() > 1;
  }

  public long count() {
    final CountingSolutionListener listener = new CountingSolutionListener();
    solver.solveGoal(goal, listener);
    return listener.count();
  }

  public BindingVar[] boundVariables() {
    final CountingSolutionListener listener = new CountingSolutionListener() {
      @Override
      public Integer onSolution(UnifyContext currentVars) {
        for (final BindingVar bv : bindingVars) {
          final Object reify = currentVars.reify(bv);
          bv.addResult(reify);
        }
        return super.onSolution(currentVars);
      }
    };
    solver.solveGoal(goal, listener);
    return bindingVars;
  }

  /**
   * @return Solution to the whole goal. If the goal was a(X), will return a(1), a(2), etc.
   */
  public SolutionHolder<Object> solution() {
    return new SolutionHolder<Object>(this, Var.WHOLE_SOLUTION_VAR_NAME, Object.class);
  }

  /**
   * Seek solutions for only one variable of the goal, of the desired type. Does not yet execute the goal.
   *
   * @param varName             The name of the variable to solve for.
   * @param desiredTypeOfResult
   * @param <T>
   * @return A SolutionHolder for only the specified variable.
   */
  public <T> SolutionHolder<T> var(String varName, Class<? extends T> desiredTypeOfResult) {
    final SolutionHolder<T> solutionHolder = new SolutionHolder<T>(this, varName, desiredTypeOfResult);
    return solutionHolder;
  }

  /**
   * Seek solutions for only one variable of the goal, of the desired type. Does not yet execute the goal.
   *
   * @param var                 The variable to solve for.
   * @param desiredTypeOfResult
   * @param <T>
   * @return A SolutionHolder for only the specified variable.
   */
  public <T> SolutionHolder<T> var(Var<T> var, Class<? extends T> desiredTypeOfResult) {
    // FIXME temporary implementation this should be the principal implementation (not the one by name)
    return var(var.getName(), desiredTypeOfResult);
  }

  /**
   * Seek solutions for only one variable of the goal, of any type.
   *
   * @param varName The name of the variable to solve for.
   * @return A SolutionHolder for only the specified variable.
   */
  public SolutionHolder<Object> var(String varName) {
    return var(varName, Object.class);
  }

  /**
   * Seek solutions for only one variable of the goal, of any type.
   *
   * @param var The name of the variable to solve for.
   * @return A SolutionHolder for only the specified variable.
   */
  public <T> SolutionHolder<T> var(Var<T> var) {
    return var(var, var.getType());
  }

  public SolutionHolder<Map<Var<?>, Object>> vars() {
    return SolutionHolder.extractingMaps(this);
  }

  // --------------------------------------------------------------------------
  // Accessors
  // --------------------------------------------------------------------------


  public Solver getSolver() {
    return solver;
  }

  public Object getGoal() {
    return goal;
  }


  // ---------------------------------------------------------------------------
  // Syntactic sugars
  // ---------------------------------------------------------------------------


  public Object intValue(String varName) {
    return var(varName, Integer.class).unique();
  }

  //  public String toString(String varName) {
  //    return var(varName).unique().toString();
  //  }

}
