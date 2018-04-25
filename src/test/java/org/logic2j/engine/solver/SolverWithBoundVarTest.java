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
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.Even;
import org.logic2j.engine.predicates.Odd;
import org.logic2j.engine.solver.holder.GoalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.model.Var.strVar;
import static org.logic2j.engine.predicates.Predicates.eq;

public class SolverWithBoundVarTest {
  private static final Logger logger = LoggerFactory.getLogger(SolverWithBoundVarTest.class);
  private final Solver solver = new Solver();

  @Test
  public void supplyFromBoundVar() {
    final Var<Integer> Q = intVar("Q");
    final Term goal = eq(Q, Q);
    final GoalHolder holder = solver.solve(goal).withBoundVar(Q, bind(IntStream.range(1, 20).boxed()));
    assertThat(holder.count()).isEqualTo(19L);
    final List<Integer> list = holder.var(Q).list();
    assertThat(list.toString()).isEqualTo("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]");
  }

  @Test
  public void supplyFrom2BoundVarS() {
    final Var<String> S = strVar("S");
    final Var<Integer> Q = intVar("Q");
    final List<String> list = solver.solve(eq(S, S), eq(Q, Q)).withBoundVar(S, bind("A", "B")).withBoundVar(Q, bind(1, 2, 3)).var(S).list();
    assertThat(list.toString()).isEqualTo("[A, A, A, B, B, B]");
  }

  @Test
  public void supplyFrom2BoundVarQ() {
    final Var<String> S = strVar("S");
    final Var<Integer> Q = intVar("Q");
    final List<Integer> list = solver.solve(eq(S, S), eq(Q, Q)).withBoundVar(S, bind("A", "B")).withBoundVar(Q, bind(1, 2, 3)).var(Q).list();
    assertThat(list.toString()).isEqualTo("[1, 2, 3, 1, 2, 3]");
  }

  @Test
  public void supplyFrom2BoundVarInverse() {
    final Var<Integer> Q = intVar("Q");
    final Term goal = eq(Q, Q);
    final List<Integer> list = solver.solve(goal).withBoundVar(Q,  bind(IntStream.range(1, 20).boxed())).var(Q).list();
    assertThat(list.toString()).isEqualTo("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]");
  }

  @Test
  public void supplyAndFilterBoundVar() {
    final Var<Integer> Q = intVar("Q");
    final Term goal = new Even(Q);
    final List<Integer> list = solver.solve(goal).withBoundVar(Q, bind(IntStream.range(1, 20).boxed())).var(Q).list();
    assertThat(list.toString()).isEqualTo("[2, 4, 6, 8]");
  }


  @Test
  public void retrieveCrossProductFromBoundVar() {
    final Var<Integer> Q = intVar("Q");
    final Var<Integer> R = intVar("R");

    final GoalHolder holder = solver.solve(new Even(Q), new Odd(R));
    final List<Integer> qs = holder.var(Q).list();
    final List<Integer> rs = holder.var(R).list();
    logger.info("Result: {}", qs);
    logger.info("Result: {}", rs);
    assertThat(qs.toString()).isEqualTo("[0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8]");
    assertThat(rs.toString()).isEqualTo("[1, 3, 5, 7, 9, 1, 3, 5, 7, 9, 1, 3, 5, 7, 9, 1, 3, 5, 7, 9, 1, 3, 5, 7, 9]");
  }

}