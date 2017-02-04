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
import org.logic2j.engine.model.SimpleBinding;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.Digit;
import org.logic2j.engine.predicates.Odd;
import org.logic2j.engine.predicates.impl.io.logging.Debug;
import org.logic2j.engine.predicates.impl.io.logging.Error;
import org.logic2j.engine.predicates.impl.io.logging.Info;
import org.logic2j.engine.predicates.impl.io.logging.Log;
import org.logic2j.engine.predicates.impl.io.logging.Warn;
import org.logic2j.engine.predicates.impl.math.Abs;
import org.logic2j.engine.predicates.impl.math.Succ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.model.SimpleBinding.arr;
import static org.logic2j.engine.model.SimpleBinding.cst;
import static org.logic2j.engine.model.Var.doubleVar;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.predicates.Predicates.and;
import static org.logic2j.engine.predicates.Predicates.anon;
import static org.logic2j.engine.predicates.Predicates.exists;
import static org.logic2j.engine.predicates.Predicates.fail;
import static org.logic2j.engine.predicates.Predicates.filter;
import static org.logic2j.engine.predicates.Predicates.map;
import static org.logic2j.engine.predicates.Predicates.not;

public class PredicatesTest {
  private static final Logger logger = LoggerFactory.getLogger(PredicatesTest.class);
  private SolverApi solver = new SolverApi();


  @Test
  public void usingPlainJavaPredicate() {
    final Var<Integer> Q = intVar("Q");
    final List<Integer> list = solver.solve(new Digit(Q), new Odd(Q), filter(Q, i -> i != 5)).var(Q).list();
    logger.info("Result: {}", list);
    assertThat(list.toString(), is("[1, 3, 7, 9]"));
  }

  @Test
  public void usingPlainJavaFunction() {
    final Var<Integer> Q = intVar("Q");
    final Var<Integer> R = intVar("R");
    final List<Integer> list = solver.solve(new Digit(Q), map(Q, x -> x * x, R)).var(R).list();
    logger.info("Result: {}", list);
    assertThat(list.toString(), is("[0, 1, 4, 9, 16, 25, 36, 49, 64, 81]"));
  }

  // --------------------------------------------------------------------------
  // Logging predicates
  // --------------------------------------------------------------------------

  @Test
  public void trace() {
    final long nbr = solver.solve(new Log("trace", "voluntary trace message - from test case")).count();
    assertThat(nbr, is(1L));
  }

  @Test
  public void debug() {
    final long nbr = solver.solve(new Debug("voluntary debug message - from test case")).count();
    assertThat(nbr, is(1L));
  }

  @Test
  public void info() {
    final long nbr = solver.solve(new Info("voluntary info message - from test case")).count();
    assertThat(nbr, is(1L));
  }

  @Test
  public void warn() {
    final long nbr = solver.solve(new Warn("voluntary warning message - from test case")).count();
    assertThat(nbr, is(1L));
  }

  @Test
  public void error() {
    final long nbr = solver.solve(new Error("voluntary error message - from test case")).count();
    assertThat(nbr, is(1L));
  }

  // --------------------------------------------------------------------------
  // First-order logic
  // --------------------------------------------------------------------------

  @Test
  public void testExists() {
    final long nbr = solver.solve(exists(solver, and(new Digit(anon()), new Info("sol")))).count();
    assertThat(nbr, is(1L));
  }

  @Test
  public void testNot1() {
    final long nbr = solver.solve(not(solver, and(new Digit(anon()), new Info("sol")))).count();
    assertThat(nbr, is(0L));
  }

  @Test
  public void testNot2() {
    final long nbr = solver.solve(not(solver, fail)).count();
    assertThat(nbr, is(1L));
  }

  // --------------------------------------------------------------------------
  // Test the "Succ" predicate that can resolve forward or reverse, using either Var or single constant
  // --------------------------------------------------------------------------

  @Test
  public void succTwoVars() {
    assertThat(solver.solve(new Succ<>(intVar("X"), intVar("Y"))).count(), is(0L));
  }

  @Test
  public void succIntCheckOk() {
    assertThat(solver.solve(new Succ<>(5, 6)).count(), is(1L));
  }

  @Test
  public void succIntCheckNOk() {
    assertThat(solver.solve(new Succ<>(5, 7)).count(), is(0L));
  }

  @Test
  public void succDoubleCheckOk() {
    assertThat(solver.solve(new Succ<>(5.0, 6.0)).count(), is(1L));
  }

  @Test
  public void succDoubleCheckNOk() {
    assertThat(solver.solve(new Succ<>(5.0, 6.1)).count(), is(0L));
  }

  @Test
  public void succIntForward() {
    final Var<Integer> Q = intVar("Q");
    assertThat(solver.solve(new Succ<>(5, Q)).var(Q).list().toString(), is("[6]"));
  }

  @Test
  public void succDoubleForward() {
    final Var<Double> Q = doubleVar("Q");
    assertThat(solver.solve(new Succ<>(5.1, Q)).var(Q).list().toString(), is("[6.1]"));
  }

  @Test
  public void succIntReverse() {
    final Var<Integer> Q = intVar("Q");
    assertThat(solver.solve(new Succ<>(Q, 5)).var(Q).list().toString(), is("[4]"));
  }

  @Test
  public void succDoubleReverse() {
    final Var<Double> Q = doubleVar("Q");
    assertThat(solver.solve(new Succ<>(Q, 5.1)).var(Q).list().toString(), is("[4.1]"));
  }

  // --------------------------------------------------------------------------
  // Test the "Succ" predicate that can resolve forward or reverse, using either Var or a SimpleBinding
  // --------------------------------------------------------------------------

  @Test
  public void succIntsCheckOkFully() {
    assertThat(solver.solve(new Succ<>(arr(5, 6, 7), arr(6, 7, 8))).count(), is(3L));
  }

  @Test
  public void succIntsCheckOk() {
    assertThat(solver.solve(new Succ<>(arr(5, 6, 7), arr(1, 7, 10, 8))).count(), is(2L));
  }

  @Test
  public void succInts0Forward() {
    final Var<Integer> Q = intVar("Q");
    assertThat(solver.solve(new Succ<>(new SimpleBinding<>(Integer.class), Q)).var(Q).list().toString(), is("[]"));
  }

  @Test
  public void succInts1Forward() {
    final Var<Integer> Q = intVar("Q");
    assertThat(solver.solve(new Succ<>(cst(5), Q)).var(Q).list().toString(), is("[6]"));
  }

  @Test
  public void succInts2Forward() {
    final Var<Integer> Q = intVar("Q");
    assertThat(solver.solve(new Succ<>(arr(5, 6, 7), Q)).var(Q).list().toString(), is("[6, 7, 8]"));
  }

  @Test
  public void succInts2Reverse() {
    final Var<Integer> Q = intVar("Q");
    assertThat(solver.solve(new Succ<>(Q, arr(5, 6, 7))).var(Q).list().toString(), is("[4, 5, 6]"));
  }


  // --------------------------------------------------------------------------
  // Test the "Abs" where every image has 2 preimages (except zero)
  // --------------------------------------------------------------------------

  @Test
  public void absInts3Forward() {
    final Var<Integer> Q = intVar("Q");
    assertThat(solver.solve(new Abs<>(arr(-5, 0, 6), Q)).var(Q).list().toString(), is("[5, 0, 6]"));
  }

  @Test
  public void absInts3Reverse() {
    final Var<Integer> Q = intVar("Q");
    assertThat(solver.solve(new Abs<>(Q, arr(-5, 0, 6))).var(Q).list().toString(), is("[0, -6, 6]"));
  }

}