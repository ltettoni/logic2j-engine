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

package org.logic2j.engine.predicates;

import org.junit.Test;
import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.predicates.impl.math.compare.LT;
import org.logic2j.engine.solver.SolverApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.Var.intVar;

/**
 * Test the comparison operators.
 */
public class Comp2Test {
  private static final Logger logger = LoggerFactory.getLogger(Comp2Test.class);
  private SolverApi solver = new SolverApi();

  @Test(expected=SolverException.class)
  public void twoVars() {
    Term goal = new LT(intVar(), intVar());
    assertThat(solver.solve(goal).exists(), is(false));
  }

  @Test(expected=SolverException.class)
  public void var1() {
    Term goal = new LT(intVar(), 123);
    assertThat(solver.solve(goal).exists(), is(false));
  }

  @Test(expected=SolverException.class)
  public void var2() {
    Term goal = new LT(intVar(), intVar());
    assertThat(solver.solve(goal).exists(), is(false));
  }

  @Test
  public void valid1() {
    Term goal = new LT(bind(10), bind(20));
    assertThat(solver.solve(goal).count(), is(1L));
  }

  @Test
  public void valid2() {
    Term goal = new LT(bind(10, 11), bind(20, 21, 22));
    assertThat(solver.solve(goal).count(), is(6L));
  }

  @Test
  public void valid3() {
    Term goal = new LT(bind(10, 11), bind(20, 11, 22));
    assertThat(solver.solve(goal).count(), is(5L));
  }

  @Test
  public void invalid1() {
    Term goal = new LT(bind(20), bind(10, 11));
    assertThat(solver.solve(goal).count(), is(0L));
  }

  @Test
  public void invalid2() {
    Term goal = new LT(bind(), bind(10, 11));
    assertThat(solver.solve(goal).count(), is(0L));
  }

  @Test
  public void invalid3() {
    Term goal = new LT(bind(20), bind());
    assertThat(solver.solve(goal).count(), is(0L));
  }
}
