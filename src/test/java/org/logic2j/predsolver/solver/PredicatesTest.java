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

package org.logic2j.predsolver.solver;

import org.junit.Test;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.Digit;
import org.logic2j.predsolver.predicates.Odd;
import org.logic2j.predsolver.predicates.impl.io.logging.Debug;
import org.logic2j.predsolver.predicates.impl.io.logging.Info;
import org.logic2j.predsolver.predicates.impl.io.logging.Log;
import org.logic2j.predsolver.predicates.impl.io.logging.Warn;
import org.logic2j.predsolver.predicates.impl.io.logging.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.predsolver.model.Var.intVar;
import static org.logic2j.predsolver.predicates.Predicates.filter;
import static org.logic2j.predsolver.predicates.Predicates.map;

public class PredicatesTest {
  private static final Logger logger = LoggerFactory.getLogger(PredicatesTest.class);
  private SolverApi solver = new SolverApi();


  @Test
  public void usingPlainJavaPredicate() {
    final Var<Integer> Q = intVar("Q");
    final List<Object> list = solver.solve(new Digit(Q), new Odd(Q), filter(Q, i -> i != 5)).var("Q").list();
    logger.info("Result: {}", list);
    assertThat(list.toString(), is("[1, 3, 7, 9]"));
  }

  @Test
  public void usingPlainJavaFunction() {
    final Var<Integer> Q = intVar("Q");
    final Var<Integer> R = intVar("R");
    final List<Object> list = solver.solve(new Digit(Q), map(Q, x -> x*x, R)).var("R").list();
    logger.info("Result: {}", list);
    assertThat(list.toString(), is("[0, 1, 4, 9, 16, 25, 36, 49, 64, 81]"));
  }

  // --------------------------------------------------------------------------
  // Logging predicates
  // --------------------------------------------------------------------------

  @Test
  public void trace() {
    final long nbr = solver.solve(new Log("trace", "intentional trace message - from test case")).count();
    assertThat(nbr, is(1L));
  }

  @Test
  public void debug() {
    final long nbr = solver.solve(new Debug("intentional debug message - from test case")).count();
    assertThat(nbr, is(1L));
  }

  @Test
  public void info() {
    final long nbr = solver.solve(new Info("intentional info message - from test case")).count();
    assertThat(nbr, is(1L));
  }

  @Test
  public void warn() {
    final long nbr = solver.solve(new Warn("intentional warning message - from test case")).count();
    assertThat(nbr, is(1L));
  }

  @Test
  public void error() {
    final long nbr = solver.solve(new Error("intentional error message - from test case")).count();
    assertThat(nbr, is(1L));
  }


  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

}