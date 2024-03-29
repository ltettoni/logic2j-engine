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

import org.junit.Test;
import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.generator.Digit;
import org.logic2j.engine.predicates.impl.generator.Even;
import org.logic2j.engine.predicates.impl.firstorder.Count;
import org.logic2j.engine.predicates.impl.generator.Odd;
import org.logic2j.engine.predicates.internal.And;
import org.logic2j.engine.predicates.internal.Call;
import org.logic2j.engine.predicates.internal.Optional;
import org.logic2j.engine.predicates.internal.Or;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.predicates.Predicates.*;

public class FirstOrderLogicTest {
  private final Solver solver = new Solver();

  @Test
  public void sampleData() {
    final Var<Integer> X = intVar("X");
    final Or or = or(new Digit(X), new Even(X));
    final List<Integer> list = solver.solve(or).var(X).list();
    assertThat(list.toString()).isEqualTo("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 4, 6, 8]");
  }

  @Test
  public void oddAndEven() {
    final Var<Integer> X = intVar("X");
    final And and = and(new Odd(X), new Even(X));
    final List<Integer> list = solver.solve(and).var(X).list();
    assertThat(list).isEmpty();
  }

  // --------------------------------------------------------------------------
  // Testing call()
  // --------------------------------------------------------------------------


  @Test
  public void callFixed() {
    final Var<Integer> X = intVar("X");
    final Or or = or(new Digit(X), new Even(X));
    final List<Integer> list = solver.solve(new Call(or)).var(X).list();
    assertThat(list.toString()).isEqualTo("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 4, 6, 8]");
  }

  @Test(expected = SolverException.class)
  public void callOnFreeVar() {
    final Var<Integer> Z = intVar();
    assertThat(solver.solve(new Call(Z)).isPresent()).isTrue();
  }

  @Test
  public void callOnVar() {
    final Var<Integer> X = intVar("X");
    final Var<Term> Z = new Var<>(Term.class, "Z");
    final Or or = or(new Digit(X), new Even(X));
    final List<Integer> list = solver.solve(eq(Z, or), new Call(Z)).var(X).list();
    assertThat(list.toString()).isEqualTo("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 4, 6, 8]");
  }


  // --------------------------------------------------------------------------
  // Testing count()
  // --------------------------------------------------------------------------

  @Test
  public void countToVar() {
    final Var<Integer> N = intVar("N");
    final Count goal = count(new Digit(null), N);
    assertThat(solver.solve(goal).count()).isEqualTo(1);
    assertThat(solver.solve(goal).var(N).list().toString()).isEqualTo("[10]");
  }

  @Test
  public void countCheckValid() {
    final Count goal = count(new Digit(null), 10);
    assertThat(solver.solve(goal).count()).isEqualTo(1);
  }

  @Test
  public void countCheckInvalid() {
    final Count goal = count(new Digit(null), 11);
    assertThat(solver.solve(goal).count()).isEqualTo(0);
  }

  /**
   * There are 10 digits, so one solution to saying the count is either 9, 10, 11
   */
  @Test
  public void countCheckValids() {
    final Count goal = count(new Digit(null), bind(9, 10, 11));
    assertThat(solver.solve(goal).count()).isEqualTo(1);
  }

  /**
   * There are 10 digits, so two solutions to saying the count is either 9, 10, 11, 10, 13
   */
  @Test
  public void countCheckValids2() {
    final Count goal = count(new Digit(null), bind(9, 10, 11, 10, 13));
    assertThat(solver.solve(goal).count()).isEqualTo(2);
  }

  @Test
  public void countCheckValids0() {
    final Count goal = count(new Digit(null), bind(9, 11, 12, 13));
    assertThat(solver.solve(goal).count()).isEqualTo(0);
  }

  // --------------------------------------------------------------------------
  // Testing optional()
  // --------------------------------------------------------------------------
  @Test
  public void optionalWithSolutions() {
    final Var<Integer> X = intVar("X");
    final And and = and(new Digit(X), new Even(X));
    final List<Integer> list = solver.solve(new Optional(and)).var(X).list();
    assertThat(list.toString()).isEqualTo("[0, 2, 4, 6, 8]");
  }

  /**
   * Using Optional on a goal that has no numeric solution (even AND odd) will still
   * yield the free variable, so one solution.
   * The pendant of this test yields zero results, see {{@link #oddAndEven()}
   */
  @Test
  public void optionalWithoutSolutions() {
    final Var<Integer> X = intVar("X");
    final And and = and(new Odd(X), new Even(X));
    final List<Integer> list = solver.solve(new Optional(and)).var(X).list();
    assertThat(list.toString()).isEqualTo("[X]");
  }

}
