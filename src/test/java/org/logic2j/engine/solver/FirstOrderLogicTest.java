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
import org.logic2j.engine.predicates.Digit;
import org.logic2j.engine.predicates.Even;
import org.logic2j.engine.predicates.impl.firstorder.Count;
import org.logic2j.engine.predicates.internal.Call;
import org.logic2j.engine.predicates.internal.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.model.Var.longVar;
import static org.logic2j.engine.predicates.Predicates.count;
import static org.logic2j.engine.predicates.Predicates.eq;
import static org.logic2j.engine.predicates.Predicates.or;

public class FirstOrderLogicTest {
  private static final Logger logger = LoggerFactory.getLogger(FirstOrderLogicTest.class);
  private SolverApi solver = new SolverApi();

  @Test
  public void sampleData() {
    final Var<Integer> X = intVar("X");
    final Or or = or(new Digit(X), new Even(X));
    final List<Integer> list = solver.solve(or).var(X).list();
    assertThat(list.toString(), is("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 4, 6, 8]"));
  }

  @Test
  public void callFixed() {
    final Var<Integer> X = intVar("X");
    final Or or = or(new Digit(X), new Even(X));
    final List<Integer> list = solver.solve(new Call(or)).var(X).list();
    assertThat(list.toString(), is("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 4, 6, 8]"));
  }

  @Test(expected = SolverException.class)
  public void callOnFreeVar() {
    final Var<Integer> Z = intVar();
    solver.solve(new Call(Z)).exists();
  }

  @Test
  public void callOnVar() {
    final Var<Integer> X = intVar("X");
    final Var<Term> Z = new Var<>(Term.class, "Z");
    final Or or = or(new Digit(X), new Even(X));
    final List<Integer> list = solver.solve(eq(Z, or), new Call(Z)).var(X).list();
    assertThat(list.toString(), is("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 4, 6, 8]"));
  }


  // --------------------------------------------------------------------------
  // Testing count()
  // --------------------------------------------------------------------------


  @Test
  public void countToVar() {
    final Var<Long> N = longVar("N");
    final Count goal = count(new Digit(null), N);
    assertThat(solver.solve(goal).count(), is(1L));
    assertThat(solver.solve(goal).var(N).list().toString(), is("[10]"));
  }

  @Test
  public void countCheckValid() {
    final Count goal = count(new Digit(null), 10);
    assertThat(solver.solve(goal).count(), is(1L));
  }

  @Test
  public void countCheckInvalid() {
    final Count goal = count(new Digit(null), 11);
    assertThat(solver.solve(goal).count(), is(0L));
  }


  @Test
  public void countCheckValids() {
    final Count goal = count(new Digit(null), bind(9L, 10L, 11L));
    assertThat(solver.solve(goal).count(), is(1L));
  }

  @Test
  public void countCheckValids2() {
    final Count goal = count(new Digit(null), bind(9L, 10L, 11L, 10L, 13L));
    assertThat(solver.solve(goal).count(), is(2L));
  }

  @Test
  public void countCheckValids0() {
    final Count goal = count(new Digit(null), bind(9L, 11L, 12L, 13L));
    assertThat(solver.solve(goal).count(), is(0L));
  }


}