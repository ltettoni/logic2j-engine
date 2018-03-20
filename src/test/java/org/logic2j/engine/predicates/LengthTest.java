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
import org.logic2j.engine.model.Term;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.math.function.Length;
import org.logic2j.engine.solver.SolverApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.model.Var.strVar;

/**
 * Test the Length operator.
 */
public class LengthTest {
  private static final Logger logger = LoggerFactory.getLogger(LengthTest.class);
  private SolverApi solver = new SolverApi();

  @Test
  public void twoVars() {
    final Term goal = new Length(strVar(), intVar());
    assertThat(solver.solve(goal).exists(), is(false));
  }

  @Test
  public void var1() {
    final Term goal = new Length(strVar(), bind(123));
    assertThat(solver.solve(goal).exists(), is(false));
  }

  @Test
  public void var2() {
    final Var<Integer> len = intVar();
    final Term goal = new Length(bind("toto"), len);
    assertThat(solver.solve(goal).var(len).list().toString(), is("[4]"));
  }

  @Test
  public void var22() {
    final Var<Integer> len = intVar();
    final Term goal = new Length(bind("a", "little", "dog"), len);
    assertThat(solver.solve(goal).var(len).list().toString(), is("[1, 6, 3]"));
  }

}
