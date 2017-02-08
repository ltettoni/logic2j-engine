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
import org.logic2j.engine.model.SimpleBinding;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.Even;
import org.logic2j.engine.predicates.Odd;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.predicates.impl.io.logging.Info;
import org.logic2j.engine.predicates.impl.math.Succ;
import org.logic2j.engine.solver.holder.BindingVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.predicates.Predicates.eq;
import static org.logic2j.engine.solver.holder.BindingVar.intBVar;
import static org.logic2j.engine.solver.holder.BindingVar.strBVar;

public class SolverWithSimpleBindingTest {
  private static final Logger logger = LoggerFactory.getLogger(SolverWithSimpleBindingTest.class);
  private SolverApi solver = new SolverApi();

  @Test
  public void supplyAndConsumeStream() {
    final Var<Integer> Q = intBVar("Q");
    final SimpleBinding<Integer> vals = SimpleBinding.bind(IntStream.range(1,5).boxed());
    final Term goal = new Succ<>(vals, Q);
    assertThat(solver.solve(goal).count(), is(4L));
    final List<Integer> list = solver.solve(goal).var(Q).list();
    assertThat(list.toString(), is("[2, 3, 4, 5]"));
  }

  /**
   * @see FOPredicate#constants(java.lang.Object)
   */
  @Ignore("Currently fails because we load all in memory while solving :-(")
  @Test
  public void supplyAndConsumeLargeStream() {
    final Var<Integer> Q = intBVar("Q");
    final SimpleBinding<Integer> vals = SimpleBinding.bind(new Random().ints().limit(10000000).boxed());
    final Term goal = new Succ<>(vals, Q);
    assertThat(solver.solve(goal).count(), is(10000000L));
  }

}