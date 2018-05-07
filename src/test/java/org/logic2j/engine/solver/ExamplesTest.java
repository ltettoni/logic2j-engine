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
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.Digit;
import org.logic2j.engine.predicates.impl.firstorder.Exists;
import org.logic2j.engine.predicates.impl.math.Pred2;
import org.logic2j.engine.predicates.internal.And;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.logic2j.engine.model.Var.booleanVar;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.predicates.Predicates.and;
import static org.logic2j.engine.predicates.Predicates.exists;
import static org.logic2j.engine.predicates.Predicates.not;

/**
 * Examples to explain what logic2j-engine is.
 */
public class ExamplesTest {
  private final Solver solver = new Solver();

  /**
   * @return Expression describing: Any digit X for which the reminder (base) 10 of its square is equal to X.
   */
  private And expression() {
    final Var<Integer> x = intVar("X");
    final Var<Integer> square = intVar("S");
    return and(new Digit(x), new Square(x, square), new Mod10(square, x));
  }

  /**
   * Solving the problem: find all digit integers (0..9) for which the
   * reminder (base 10) of their square is equal to themselves.
   * 4 solutions: 0, 1, 5 (=25), 6 (=36)
   */
  @Test
  public void reminder_of_square_equal_to_digit() {
    final long nbr = solver.solve(expression() /*, new Log("info", square) */).count();
    assertThat(nbr).isEqualTo(4L);
  }

  @Test
  public void exists_reminder_of_square_equal_to_digit() {
    final long nbrEx = solver.solve(exists(expression())).count();
    assertThat(nbrEx).isEqualTo(1L);
  }

  @Test
  public void exists_is_true_for_reminder_of_square_equal_to_digit() {
    final Var<Boolean> R = booleanVar("R");
    final Boolean res = solver.solve(exists(expression(), R)).var(R).single();
    assertThat(res).isTrue();
  }

  @Test
  public void exists_is_false_for_goal_without_solution() {
    final Var<Boolean> R = booleanVar("R");
    final Boolean res = solver.solve(new Exists(not(expression()), R)).var(R).single();
    assertThat(res).isFalse();
  }



  private class Square extends Pred2<Integer, Integer> {
    public Square(Binding<Integer> arg0, Binding<Integer> arg1) {
      super("square", arg0, arg1);
      setPreimage(val -> (int) Math.sqrt(val));
      setImage(val -> val * val);
    }
  }


  private class Mod10 extends Pred2<Integer, Integer> {
    public Mod10(Binding<Integer> arg0, Binding<Integer> arg1) {
      super("mod", arg0, arg1);
      setPreimage(null); // Preimage function not defined
      setImage(val -> val % 10);
    }
  }
}