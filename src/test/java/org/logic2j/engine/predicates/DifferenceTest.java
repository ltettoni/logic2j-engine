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

import static org.assertj.core.api.Assertions.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.model.Var.strVar;

import org.junit.Test;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.math.function.Length;
import org.logic2j.engine.solver.Solver;

/**
 * Test the {@link Difference} operator.
 */
public class DifferenceTest {
  private final Solver solver = new Solver();

  @Test
  public void iii() {
    final Term goal = new Difference(bind(1), bind(10), bind(11));
    assertThat(solver.solve(goal).count()).isEqualTo(1);
  }

  @Test
  public void iiv() {
    final Var<Integer> u = intVar("U");
    final Term goal = new Difference(bind(1), bind(10), u);
    assertThat(solver.solve(goal).var(u).unique()).isEqualTo(11);
  }

}
