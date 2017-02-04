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

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.predicates.Predicates.and;
import static org.logic2j.engine.predicates.Predicates.eq;
import static org.logic2j.engine.predicates.Predicates.or;

public class SolverHighLevelTest {
  private SolverApi solver = new SolverApi();

  @Test
  public void exists0() {
    final Var<Integer> Q = intVar("Q");
    final Term goal = and(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).exists(), is(false));
  }

  @Test
  public void exists2() {
    final Var<Integer> Q = intVar("Q");
    final Term goal = or(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).exists(), is(true));
  }

  @Test
  public void count0() {
    final Var<Integer> Q = intVar("Q");
    final Term goal = and(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).count(), is(0L));
  }

  @Test
  public void count2() {
    final Var<Integer> Q = intVar("Q");
    final Term goal = or(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).count(), is(2L));
  }


  @Test
  public void wholeSolution() {
    final Var<Integer> Q = intVar("Q");
    final Term goal = new Even(Q);
    final List<Object> list = solver.solve(goal).solution().list();
    assertThat(list.toString(), is("[even(0), even(2), even(4), even(6), even(8)]"));
  }

  @Test
  public void var() {
    final Var<Integer> Q = intVar("Q");
    final Term goal = new Even(Q);
    final List<Integer> list = solver.solve(goal).var(Q).list();
    assertThat(list.toString(), is("[0, 2, 4, 6, 8]"));
  }

  @Test
  public void vars() {
    final Var<Integer> Q = intVar("Q");
    final Term goal = new Even(Q);
    final List<Map<Var<?>, Object>> list = solver.solve(goal).vars().list();
    assertThat(list.toString(), is("[{Q=0}, {Q=2}, {Q=4}, {Q=6}, {Q=8}]"));
  }


  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

}