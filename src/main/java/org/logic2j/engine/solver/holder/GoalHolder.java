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

import org.logic2j.api.result.ResultsHolder;
import org.logic2j.engine.exception.InvalidTermException;
import org.logic2j.engine.model.Constant;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.Eq;
import org.logic2j.engine.solver.Solver;
import org.logic2j.engine.solver.extractor.ObjectFactory;
import org.logic2j.engine.solver.listener.CountingSolutionListener;
import org.logic2j.engine.solver.listener.ExistsSolutionListener;
import org.logic2j.engine.solver.listener.SolutionListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.predicates.Predicates.and;

/**
 * An intermediate class in the fluent API to extract solutions; a GoalHolder holds the state of what the user
 * wishes to calculate, being either the existence of, the number of solutions, or individual or multiple values
 * of one particular variable or of all vars.
 * This object will launch the solver only for methods exists() or count(). For other
 * methods it just returns instances of SolutionHolder which further delays the execution.
 */
public class GoalHolder implements ResultsHolder<Object> {

  private final Solver solver;
  private final Object goal;
  private Object effectiveGoal;
  private final BiFunction<Object, Class, Object> termToSolutionFunction;
  private final LinkedHashMap<Var, Constant> varBindings;

  public GoalHolder(Solver solver, Object theGoal, BiFunction<Object, Class, Object> termToSolutionFunction) {
    this.solver = solver;
    this.goal = theGoal;
    this.effectiveGoal = null;
    this.termToSolutionFunction = termToSolutionFunction;
    this.varBindings = new LinkedHashMap<>();
  }

  /**
   * Entry point for solving, in case we have variable bound to values, we will prepend the goal with
   * Eq/2 predicates that will bind the variables to the specified values.
   * We do not make this method public since implementing SolutionListener requires to know the gutts
   * of the {@link org.logic2j.engine.unify.UnifyContext}.
   *
   * @param listener Callback for each solution
   * @return Continuation
   */
  private int solve(SolutionListener listener) {
    return solver.solveGoal(effectiveGoal(), listener);
  }

  /**
   * Based on the existence of bindings of free vars to constants, see {@link #withBoundVar(Var, Constant)},
   * modify the predefined goal to add Eq/2 predicates to bind the free vars to real values.
   *
   * @return A potentially modified goal, otherwise the value of {@link #getGoal()}
   */
  public Object effectiveGoal() {
    if (effectiveGoal != null) {
      return effectiveGoal;
    }
    if (varBindings.isEmpty()) {
      effectiveGoal = getGoal();
    } else {
      if (!(getGoal() instanceof Term)) {
        throw new InvalidTermException("Goal for solving must be a Term: " + getGoal());
      }
      // Create the "and" conjunction of all equality predicates used to bind variables to their values, and then the original goal.
      final List<Term> effectiveGoals = new ArrayList<>();
      for (Map.Entry<Var, Constant> binding : varBindings.entrySet()) {
        Constant values = binding.getValue();
        final Var<?> var = binding.getKey();
        if (values.isUniqueFeed()) {
          values = bind(values.toArray());
        }
        final Eq toBindVar = new Eq(var, values);
        effectiveGoals.add(toBindVar);
      }
      effectiveGoals.add((Term) getGoal());
      final Term and = and(effectiveGoals.toArray(new Term[0]));
      effectiveGoal = termApi().normalize(and);
    }
    return effectiveGoal;
  }


  /**
   * @return True if at least one solution can be demonstrated. Solving will stop at the first solution.
   */
  @Override
  public boolean isPresent() {
    final ExistsSolutionListener listener = new ExistsSolutionListener();
    solve(listener);
    return listener.exists();
  }

  /**
   * @return true if there is exactly ONE solution to the goal.
   */
  @Override
  public boolean isUnique() {
    final CountingSolutionListener listener = new CountingSolutionListener(2); // Stop seeking solution after 2 as the goal is to prove unicity
    solve(listener);
    return listener.count() == 1;
  }

  @Override
  public List<Object> list() {
    return null;
  }

  /**
   * @return true if there are more than one solution to the goal.
   */
  @Override
  public boolean isMultiple() {
    final CountingSolutionListener listener = new CountingSolutionListener(2); // Stop seeking solution after 2 as the goal is to prove multiplicity
    solve(listener);
    return listener.count() > 1;
  }

  /**
   * @return Exact number of all enumerated solutions to the goal.
   */
  @Override
  public int count() {
    final CountingSolutionListener listener = new CountingSolutionListener();
    solve(listener);
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
   * @param varName The name of the variable to solve for.
   * @return A SolutionHolder for only the specified variable.
   */
  public SolutionHolder<Object> var(String varName) {
    return new SolutionHolder<>(this, varName, Object.class, this.termToSolutionFunction);
  }

  /**
   * Seek solutions for only one variable of the goal, of any type.
   *
   * @param var The name of the variable to solve for.
   * @return A SolutionHolder for only the specified variable.
   */
  public <T> SolutionHolder<T> var(Var<T> var) {
    // FIXME temporary implementation this should be the principal implementation (not the one by name)
    return var(var.getName(), var.getType());
  }


  public SolutionHolder<Map<Var<?>, Object>> vars() {
    return SolutionHolder.extractingMaps(this);
  }

  public SolutionHolder<Map<Var<?>, Object>> vars(Var<?>... vars) {
    return SolutionHolder.extractingMaps(this, vars);
  }

  @Override
  public <R> ResultsHolder<R> map(Function<Object, R> mapping) {
    throw new UnsupportedOperationException("Not implemented");
  }

  // --------------------------------------------------------------------------
  // Accessors
  // --------------------------------------------------------------------------

  public Solver getSolver() {
    return solver;
  }

  /**
   * Public users, use {@link #effectiveGoal()} instead.
   *
   * @return The original goal
   */
  private Object getGoal() {
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
   * @return A SolutionHolder that returns solutions as array of Objects, for the speicified variables
   * @param vars The variables of interest
   */
  public SolutionHolder<Object[]> varsArray(Var<?>... vars) {
    return SolutionHolder.extractingArrays(this, vars);
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


  public <T> GoalHolder withBoundVar(Var<T> var, Constant<T> binding) {
    varBindings.put(var, binding);
    return this;
  }

  public String toString() {
    return this.getClass().getSimpleName() + "(" + this.getGoal() + ")";
  }

}
