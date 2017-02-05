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
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.Digit;
import org.logic2j.engine.predicates.Even;
import org.logic2j.engine.predicates.internal.Call;
import org.logic2j.engine.predicates.internal.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.predicates.Predicates.or;
import static org.logic2j.engine.predicates.Predicates.ttrue;

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
  public void call() {
    assertThat(solver.solve(new Call(ttrue)).count(), is(1L));
  }

  @Test
  public void call1() {
    final Var<Integer> X = intVar("X");
    assertThat(solver.solve(new Call(new Digit(X))).count(), is(10L));
  }

  @Test
  public void callFixed() {
    final Var<Integer> X = intVar("X");
    final Or or = or(new Digit(X), new Even(X));
    final List<Object> list = solver.solve(new Call(or)).var("X").list();
    assertThat(list.toString(), is("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 4, 6, 8]"));
  }
}