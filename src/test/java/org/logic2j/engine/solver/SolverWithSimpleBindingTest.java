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

import org.junit.Ignore;
import org.junit.Test;
import org.logic2j.engine.model.Constant;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.math.Succ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.solver.holder.BindingVar.intBVar;

public class SolverWithSimpleBindingTest {
  private static final Logger logger = LoggerFactory.getLogger(SolverWithSimpleBindingTest.class);
  private SolverApi solver = new SolverApi();

  @Test
  public void supplyAndConsumeStream1() {
    final Var<Integer> Q = intBVar("Q");
    final Constant<Integer> vals = bind(IntStream.range(1,5).boxed());
    final Term goal = new Succ<>(vals, Q);
    assertThat(solver.solve(goal).count(), is(4L));
  }

  @Test
  public void supplyAndConsumeStream2() {
    final Var<Integer> Q = intBVar("Q");
    final Constant<Integer> vals = bind(IntStream.range(1,5).boxed());
    final Term goal = new Succ<>(vals, Q);
    final List<Integer> list = solver.solve(goal).var(Q).list();
    assertThat(list.toString(), is("[2, 3, 4, 5]"));
  }

  @Ignore("Currently fails because we load all in memory while solving :-(")
  @Test
  public void supplyAndConsumeLargeStream() {
    final Var<Integer> Q = intBVar("Q");
    final Constant<Integer> vals = bind(new Random().ints().limit(10000000).boxed());
    final Term goal = new Succ<>(vals, Q);
    assertThat(solver.solve(goal).count(), is(10000000L));
  }

}