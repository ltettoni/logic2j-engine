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
import org.logic2j.engine.solver.extractor.ObjectFactory;
import org.logic2j.engine.solver.listener.CountingSolutionListener;
import org.logic2j.engine.solver.listener.ExistsSolutionListener;

import java.util.Map;
import java.util.function.BiFunction;

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
  private final BiFunction<Object, Class, Object> termToSolutionFunction;

  public GoalHolder(Solver solver, Object theGoal, BiFunction<Object, Class, Object> termToSolutionFunction) {
    this.solver = solver;
    this.goal = theGoal;
    this.termToSolutionFunction = termToSolutionFunction;
  }

  /**
   * @return True if at least one solution can be demonstrated. Solving will stop at the first solution.
   */
  public boolean exists() {
    final ExistsSolutionListener listener = new ExistsSolutionListener();
    solver.solveGoal(goal, listener);
    return listener.exists();
  }

  /**
   * @return True if no solution could be demonstrated
   */
  public boolean none() {
    return !exists();
  }

  /**
   * @return true if there is exactly ONE solution to goal.
   */
  public boolean unique() {
    final CountingSolutionListener listener = new CountingSolutionListener(2);
    solver.solveGoal(goal, listener);
    return listener.count() == 1;
  }

  /**
   * @return true if there are more than one solution.
   */
  public boolean multiple() {
    final CountingSolutionListener listener = new CountingSolutionListener(2);
    solver.solveGoal(goal, listener);
    return listener.count() > 1;
  }

  public long count() {
    final CountingSolutionListener listener = new CountingSolutionListener();
    solver.solveGoal(goal, listener);
    return listener.count();
  }


  /**
   * @return Solution to the whole goal. If the goal was a(X), will return a(1), a(2), etc.
   */
  public SolutionHolder<Object> solution() {
    return new SolutionHolder<>(this, Var.WHOLE_SOLUTION_VAR_NAME, Object.class, this.termToSolutionFunction);
  }

  /**
   * Seek solutions for only one variable of the goal, of the desired type. Does not yet execute the goal.
   *
   * @param <T>
   * @param varName             The name of the variable to solve for.
   * @param desiredTypeOfResult
   * @return A SolutionHolder for only the specified variable.
   */
  public <T> SolutionHolder<T> var(String varName, Class<? extends T> desiredTypeOfResult) {
    return new SolutionHolder<>(this, varName, desiredTypeOfResult, this.termToSolutionFunction);
  }

  /**
   * Seek solutions for only one variable of the goal, of the desired type. Does not yet execute the goal.
   *
   * @param <T>
   * @param var                 The variable to solve for.
   * @param desiredTypeOfResult
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

  public SolutionHolder<Map<Var, Object>> vars() {
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

  public String toString(String varName) {
    return var(varName).unique().toString();
  }


  /**
   * @return A SolutionHolder that returns solutions as array of Objects
   */
  public SolutionHolder<Object[]> varsArray() {
    return SolutionHolder.extractingArrays(this);
  }

  /**
   * Instantiate objects directly.
   *
   * @param factory
   * @param <T>     Type of objects to create
   * @return A SolutionHolder for objects created by the factory
   */
  public <T> SolutionHolder<T> varsToFactory(ObjectFactory<T> factory) {
    return SolutionHolder.extractingFactory(this, factory);
  }


}
