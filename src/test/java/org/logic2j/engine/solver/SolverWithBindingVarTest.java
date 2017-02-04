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
import org.logic2j.engine.model.Term;
import org.logic2j.engine.predicates.Even;
import org.logic2j.engine.predicates.Odd;
import org.logic2j.engine.solver.holder.BindingVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.predicates.Predicates.eq;
import static org.logic2j.engine.solver.holder.BindingVar.intBVar;
import static org.logic2j.engine.solver.holder.BindingVar.strBVar;

public class SolverWithBindingVarTest {
  private static final Logger logger = LoggerFactory.getLogger(SolverWithBindingVarTest.class);
  private SolverApi solver = new SolverApi();

  @Test
  public void supplyFromBoundVar() {
    final BindingVar<Integer> Q = intBVar("Q", IntStream.range(1, 20).boxed().collect(Collectors.toList()));
    final Term goal = eq(Q, Q);
    assertThat(solver.solve(goal).count(), is(19L));
    final List<Integer> list = solver.solve(goal).var(Q).list();
    assertThat(list.toString(), is("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]"));
  }

  @Test
  public void supplyFrom2BoundVarS() {
    final BindingVar<String> S = strBVar("S", "A", "B");
    final BindingVar<Integer> Q = intBVar("Q", 1, 2, 3);
    final List<Object> list = solver.solve(eq(S, S), eq(Q, Q)).var("S").list();
    assertThat(list.toString(), is("[A, A, A, B, B, B]"));
  }

  @Test
  public void supplyFrom2BoundVarQ() {
    final BindingVar<String> S = strBVar("S", "A", "B");
    final BindingVar<Integer> Q = intBVar("Q", 1, 2, 3);
    final List<Integer> list = solver.solve(eq(S, S), eq(Q, Q)).var(Q).list();
    assertThat(list.toString(), is("[1, 2, 3, 1, 2, 3]"));
  }

  @Test
  public void supplyFrom2BoundVarInverse() {
    final BindingVar<Integer> Q = intBVar("Q", IntStream.range(1, 20).boxed().collect(Collectors.toList()));
    final Term goal = eq(Q, Q);
    final List<Integer> list = solver.solve(goal).var(Q).list();
    assertThat(list.toString(), is("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]"));
  }

  @Test
  public void supplyAndFilterBoundVar() {
    final BindingVar<Integer> Q = intBVar("Q", IntStream.range(1, 20).boxed().collect(Collectors.toList()));
    final Term goal = new Even(Q);
    final List<Integer> list = solver.solve(goal).var(Q).list();
    assertThat(list.toString(), is("[2, 4, 6, 8]"));
  }


  @Test
  public void retrieveCrossProductFromBoundVar() {
    final BindingVar<Integer> Q = intBVar("Q");
    final BindingVar<Integer> R = intBVar("R");
    final BindingVar[] boundVars = solver.solve(new Even(Q), new Odd(R)).boundVariables();
    logger.info("Result: {}", Q.getResults());
    logger.info("Result: {}", R.getResults());
    assertThat(Q.getResults().toString(), is("[0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8]"));
    assertThat(R.getResults().toString(), is("[1, 3, 5, 7, 9, 1, 3, 5, 7, 9, 1, 3, 5, 7, 9, 1, 3, 5, 7, 9, 1, 3, 5, 7, 9]"));
  }

  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

}