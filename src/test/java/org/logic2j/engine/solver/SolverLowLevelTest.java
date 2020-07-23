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
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.generator.Digit;
import org.logic2j.engine.predicates.impl.generator.Even;
import org.logic2j.engine.predicates.EvenCheck;
import org.logic2j.engine.predicates.impl.generator.IntRange;
import org.logic2j.engine.predicates.impl.firstorder.Not;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.model.Var.*;
import static org.logic2j.engine.predicates.Predicates.*;

public class SolverLowLevelTest {
  private final Solver solver = new Solver();

  // ---------------------------------------------------------------------------
  // Simplest primitives and undefined goal
  // ---------------------------------------------------------------------------

  @Test
  public void primitiveFail() {
    final int nbSolutions = solve(fail).count();
    assertThat(nbSolutions).isEqualTo(0);
  }

  @Test
  public void primitiveTrue() {
    final int nbSolutions = solve(ttrue).count();
    assertThat(nbSolutions).isEqualTo(1);
  }


  @Test
  public void dataOnlyAtom() {
    final Object goal = new Struct<>("atom");
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(0);
  }

  @Test
  public void dataOnlyStructWithParam() {
    final Object goal = new Struct<>("atom", "p1");
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(0);
  }

  @Test
  public void dataOnlyStructWithParams() {
    final Object goal = new Struct<>("atom", "p1", new Struct<>("p2", "p21", "p22"));
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(0);
  }

  @Test
  public void dataOnlyStructWithVar() {
    final Var<Object> X = anyVar("X");
    final Object goal = new Struct<>("atom", X);
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(0);
  }

  @Test
  public void dataOnlyStructWithVars() {
    final Var<Object> X = anyVar("X");
    final Var<Object> Y = anyVar("Y");
    final Object goal = new Struct<>("atom", X, Y);
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(0);
  }


  @Test
  public void primitiveTrueAndTrue() {
    final Object goal = and(ttrue, ttrue);
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(1);
  }


  @Test
  public void primitiveTrueOrTrue() {
    final Object goal = or(ttrue, ttrue);
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(2);
  }

  @Test
  public void primitiveCut() {
    final int nbSolutions = solve(cut).count();
    assertThat(nbSolutions).isEqualTo(1);
  }


  @Test
  public void orTest() {
    countNSolutions(2, or(ttrue, ttrue));
    //
    countNSolutions(3, or(ttrue, ttrue, ttrue));
    //
    final Var<Integer> Q = intVar("Q");
    countNSolutions(3, or(eq(Q, bind(1)), eq(Q, bind(2)), eq(Q, bind(3))));
    //      GoalHolder solutions;
    //      solutions = this.prolog.solve("X=a; X=b; X=c");
    //      assertEquals("[a, b, c]", solutions.var("X").list().toString());
  }


  //  /**
  //   * This is a special feature of logic2j: OR with any arity
  //   */
  //  @Test
  //  public void nonBinaryOr() {
  //    loadTheoryFromTestResourcesDir("test-functional.pro");
  //    countNSolutions(2, "';'(true, true)");
  //    if (Solver.FAST_OR) {
  //      countNSolutions(1, "';'(true)");
  //      countNSolutions(3, "';'(true, true, true)");
  //    }
  //    countNSolutions(1, "true");
  //    countNSolutions(3, "true; true; true");
  //  }
  //
  //
  //  @Test
  //  public void orWithVars() {
  //    ResultsHolder<?> solutions;
  //    solutions = this.prolog.solve("X=1; Y=2");
  //    final String actual = solutions.vars().list().toString();
  //    assertTrue("[{Y=Y, X=1}, {Y=2, X=X}]".equals(actual) ||
  //        "[{X=1, Y=Y}, {X=X, Y=2}]".equals(actual));
  //  }
  //
  //  @Test
  //  public void orWithClause() {
  //    loadTheoryFromTestResourcesDir("test-functional.pro");
  //    ResultsHolder<?> solutions;
  //    solutions = this.prolog.solve("or3(X)");
  //    assertEquals("[a, b, c]", solutions.var("X").list().toString());
  //  }

  @Test
  public void notTest() {
    countOneSolution(new Not(fail));
    countNoSolution(new Not(ttrue));
  }


  // ---------------------------------------------------------------------------
  // Basic unification using the Eq predicate
  // ---------------------------------------------------------------------------


  @Test
  public void unifyLiteralsNoSolution() {
    final Object goal = eq(bind("a"), bind("b"));
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(0);
  }


  @Test
  public void unifyLiteralsOneSolution() {
    final Object goal = eq(bind("c"), bind("c"));
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(1);
  }


  @Test
  public void unifyAnonymousToAnonymous() {
    final Object goal = eq(null, (Binding) null);
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(1);
  }


  @Test
  public void unifyVarToLiteral() {
    final Var<String> Q = strVar("Q");
    final Object goal = eq(Q, bind("d"));
    final int nbSolutions = solve(goal).count();
    assertThat(nbSolutions).isEqualTo(1);
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(1);
    assertThat(listener.getVariables().toString()).isEqualTo("[Q]");
    assertThat(marshall(listener.getValues("Q"))).isEqualTo("[d]");
    assertThat(marshall(listener.getValues("."))).isEqualTo("['='(d, Strings<d>)]");
  }


  @Test
  public void unifyVarToAnonymous() {
    final Var<String> Q = strVar("Q");
    final Object goal = eq(Q, null);
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(1);
    assertThat(listener.getVariables().toString()).isEqualTo("[Q]");
    assertThat(marshall(listener.getValues("."))).isEqualTo("['='(_, _)]");
    assertThat(marshall(listener.getValues("Q"))).isEqualTo("[_]");
  }


  @Test
  public void unifyVarToVar() {
    final Var<String> Q = strVar("Q");
    final Var<String> Z = strVar("Z");
    final Object goal = eq(Q, Z);
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(1);
    assertThat(listener.getVarNames().toString()).isEqualTo("[., Q, Z]");
    assertThat(marshall(listener.getValues("."))).isEqualTo("['='(Q, Q)]");
    assertThat(marshall(listener.getValues("Q"))).isEqualTo("[Q]");
    assertThat(marshall(listener.getValues("Z"))).isEqualTo("[Q]");
  }

  @Test
  public void unifyVarToStreamLiteral() {
    final Var<Integer> Q = intVar("Q");
    final Object goal = eq(Q, bind(IntStream.range(1, 10).boxed()));
    assertThat(solve(goal).count()).isEqualTo(9);
    //    final ExtractingSolutionListener listener = solve(goal);
    //    assertEquals(1, listener.count());
    //    assertEquals("[Q]", listener.getVariables().toString());
    //    assertEquals("[d]", marshall(listener.getValues("Q")));
    //    assertEquals("['='(d, Strings<d>)]", marshall(listener.getValues(".")));
  }

  // --------------------------------------------------------------------------
  // Digit
  // --------------------------------------------------------------------------

  @Test
  public void digitVar() {
    final Var<Integer> Q = intVar("Q");
    final Object goal = new Digit(Q);
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(10);
  }


  @Test
  public void digit0() {
    final Object goal = new Digit(bind(0));
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(1);
  }

  @Test
  public void digit9() {
    final Object goal = new Digit(bind(9));
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(1);
  }

  @Test
  public void digitNotEven() {
    final Var<Integer> Q = intVar("Q");
    final Object goal = and(new Digit(Q), not(new Even(Q)));
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(5);
  }


  @Test
  public void even8() {
    final Object goal = new Even(8);
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(1);
  }

  // --------------------------------------------------------------------------
  // Pred1
  // --------------------------------------------------------------------------

  @Test
  public void EvenCheck_12() {
    final Object goal = new EvenCheck(12);
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(1);
  }

  @Test
  public void EvenCheck_13() {
    final Object goal = new EvenCheck(13);
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(0);
  }

  @Test
  public void EvenCheck_2period0() {
    final Object goal = new EvenCheck(2.0);
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(1);
  }

  @Test
  public void EvenCheck_2period1() {
    final Object goal = new EvenCheck(2.1);
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(0);
  }

  @Test
  public void EvenCheck_3period0() {
    final Object goal = new EvenCheck(3.0);
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(0);
  }


  @Test
  public void intRangeCheckInvalid() {
    final Object goal = new IntRange(bind(10), bind(5), bind(15));
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(0);
  }

  @Test
  public void intRangeCheckValid() {
    final Object goal = new IntRange(bind(10), bind(12), bind(15));
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(1);
  }

  @Test
  public void intRangeCheckGenerate() {
    final Var<Integer> Q = intVar("Q");
    final Object goal = new IntRange(bind(10), Q, bind(15));
    final ExtractingSolutionListener listener = solve(goal);
    assertThat(listener.count()).isEqualTo(5);
  }


  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

  private LocalSolutionListener solve(Object goal) {
    final Object normalized = termApi().normalize(goal);
    final LocalSolutionListener solutionListener = new LocalSolutionListener(normalized);
    solver.solveGoal(normalized, solutionListener);
    return solutionListener;
  }


  protected String marshall(Iterable<Object> terms) {
    ArrayList<String> marshalled = new ArrayList<>();
    for (final Object term : terms) {
      marshalled.add(term.toString());
    }
    return marshalled.toString();
  }


  private static class LocalSolutionListener extends ExtractingSolutionListener {

    public LocalSolutionListener(Object goal) {
      super(goal);
    }

  }

  // ---------------------------------------------------------------------------
  // Low-lever solution counting assertions just using a CountingSolutionListener
  // ---------------------------------------------------------------------------

  protected void countOneSolution(Object... theGoals) {
    countNSolutions(1, theGoals);
  }


  protected void countNoSolution(Object... theGoals) {
    countNSolutions(0, theGoals);
  }

  protected void countNSolutions(int nbr, Object... theGoals) {
    for (final Object term : theGoals) {
      final LocalSolutionListener listener = solve(term);
      assertThat(listener.count()).as("Solving goalText \"" + term + '"').isEqualTo(nbr);
    }
  }

}