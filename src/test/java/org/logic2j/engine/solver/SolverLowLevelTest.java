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
import org.logic2j.engine.model.TermApi;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.Digit;
import org.logic2j.engine.predicates.Even;
import org.logic2j.engine.predicates.EvenCheck;
import org.logic2j.engine.predicates.IntRange;
import org.logic2j.engine.predicates.impl.firstorder.Not;
import org.logic2j.engine.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.Var.anyVar;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.model.Var.strVar;
import static org.logic2j.engine.predicates.Predicates.and;
import static org.logic2j.engine.predicates.Predicates.cut;
import static org.logic2j.engine.predicates.Predicates.eq;
import static org.logic2j.engine.predicates.Predicates.fail;
import static org.logic2j.engine.predicates.Predicates.not;
import static org.logic2j.engine.predicates.Predicates.or;
import static org.logic2j.engine.predicates.Predicates.ttrue;

public class SolverLowLevelTest {
  private static final Logger logger = LoggerFactory.getLogger(SolverLowLevelTest.class);
  private Solver solver = new Solver();

  // ---------------------------------------------------------------------------
  // Simplest primitives and undefined goal
  // ---------------------------------------------------------------------------

  @Test
  public void primitiveFail() {
    final Object goal = fail;
    final long nbSolutions = solve(goal).count();
    assertEquals(0, nbSolutions);
  }

  @Test
  public void primitiveTrue() {
    final Object goal = ttrue;
    final long nbSolutions = solve(goal).count();
    assertEquals(1, nbSolutions);
  }


  @Test
  public void dataOnlyAtom() {
    final Object goal = new Struct("atom");
    final long nbSolutions = solve(goal).count();
    assertEquals(0, nbSolutions);
  }

  @Test
  public void dataOnlyStructWithParam() {
    final Object goal = new Struct("atom", "p1");
    final long nbSolutions = solve(goal).count();
    assertEquals(0, nbSolutions);
  }

  @Test
  public void dataOnlyStructWithParams() {
    final Object goal = new Struct("atom", "p1", new Struct("p2", "p21", "p22"));
    final long nbSolutions = solve(goal).count();
    assertEquals(0, nbSolutions);
  }

  @Test
  public void dataOnlyStructWithVar() {
    final Var<Object> X = anyVar("X");
    final Object goal = new Struct("atom", X);
    final long nbSolutions = solve(goal).count();
    assertEquals(0, nbSolutions);
  }

  @Test
  public void dataOnlyStructWithVars() {
    final Var<Object> X = anyVar("X");
    final Var<Object> Y = anyVar("Y");
    final Object goal = new Struct("atom", X, Y);
    final long nbSolutions = solve(goal).count();
    assertEquals(0, nbSolutions);
  }


  @Test
  public void primitiveTrueAndTrue() {
    final Object goal = and(ttrue, ttrue);
    final long nbSolutions = solve(goal).count();
    assertEquals(1, nbSolutions);
  }


  @Test
  public void primitiveTrueOrTrue() {
    final Object goal = or(ttrue, ttrue);
    final long nbSolutions = solve(goal).count();
    assertEquals(2, nbSolutions);
  }

  @Test
  public void primitiveCut() {
    final Object goal = cut;
    final long nbSolutions = solve(goal).count();
    assertEquals(1, nbSolutions);
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
  //    GoalHolder solutions;
  //    solutions = this.prolog.solve("X=1; Y=2");
  //    final String actual = solutions.vars().list().toString();
  //    assertTrue("[{Y=Y, X=1}, {Y=2, X=X}]".equals(actual) ||
  //        "[{X=1, Y=Y}, {X=X, Y=2}]".equals(actual));
  //  }
  //
  //  @Test
  //  public void orWithClause() {
  //    loadTheoryFromTestResourcesDir("test-functional.pro");
  //    GoalHolder solutions;
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
    final long nbSolutions = solve(goal).count();
    assertEquals(0, nbSolutions);
  }


  @Test
  public void unifyLiteralsOneSolution() {
    final Object goal = eq(bind("c"), bind("c"));
    final long nbSolutions = solve(goal).count();
    assertEquals(1, nbSolutions);
  }


  @Test
  public void unifyAnonymousToAnonymous() {
    final Object goal = eq(null, (Binding) null);
    final long nbSolutions = solve(goal).count();
    assertEquals(1, nbSolutions);
  }


  @Test
  public void unifyVarToLiteral() {
    final Var<String> Q = strVar("Q");
    final Object goal = eq(Q, bind("d"));
    final long nbSolutions = solve(goal).count();
    assertEquals(1, nbSolutions);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.count());
    assertEquals("[Q]", listener.getVariables().toString());
    assertEquals("[d]", marshall(listener.getValues("Q")));
    assertEquals("['='(d, Strings<d>)]", marshall(listener.getValues(".")));
  }


  @Test
  public void unifyVarToAnonymous() {
    final Var<String> Q = strVar("Q");
    final Object goal = eq(Q, null);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.count());
    assertEquals("[Q]", listener.getVariables().toString());
    assertEquals("['='(_, _)]", marshall(listener.getValues(".")));
    assertEquals("[_]", marshall(listener.getValues("Q")));
  }


  @Test
  public void unifyVarToVar() {
    final Var<String> Q = strVar("Q");
    final Var<String> Z = strVar("Z");
    final Object goal = eq(Q, Z);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.count());
    assertEquals("[., Q, Z]", listener.getVarNames().toString());
    assertEquals("['='(Q, Q)]", marshall(listener.getValues(".")));
    assertEquals("[Q]", marshall(listener.getValues("Q")));
    assertEquals("[Q]", marshall(listener.getValues("Z")));
  }

  @Test
  public void unifyVarToStreamLiteral() {
    final Var<Integer> Q = intVar("Q");
    final Object goal = eq(Q, bind(IntStream.range(1, 10).boxed()));
    assertThat(solve(goal).count(), is(9L));
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
    assertEquals(10, listener.count());
  }


  @Test
  public void digit0() {
    final Object goal = new Digit(bind(0));
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.count());
  }

  @Test
  public void digit9() {
    final Object goal = new Digit(bind(9));
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.count());
  }

  @Test
  public void digitNotEven() {
    final Var<Integer> Q = intVar("Q");
    final Object goal = and(new Digit(Q), not(new Even(Q)));
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(5, listener.count());
  }


  @Test
  public void even8() {
    final Object goal = new Even(8);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.count());
  }

  // --------------------------------------------------------------------------
  // Pred1
  // --------------------------------------------------------------------------

  @Test
  public void EvenCheck_12() {
    final Object goal = new EvenCheck(12);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.count());
  }

  @Test
  public void EvenCheck_13() {
    final Object goal = new EvenCheck(13);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(0, listener.count());
  }

  @Test
  public void EvenCheck_2period0() {
    final Object goal = new EvenCheck(2.0);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.count());
  }

  @Test
  public void EvenCheck_2period1() {
    final Object goal = new EvenCheck(2.1);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(0, listener.count());
  }

  @Test
  public void EvenCheck_3period0() {
    final Object goal = new EvenCheck(3.0);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(0, listener.count());
  }



  @Test
  public void intRangeCheckInvalid() {
    final Object goal = new IntRange(bind(10), bind(5), bind(15));
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(0, listener.count());
  }

  @Test
  public void intRangeCheckValid() {
    final Object goal = new IntRange(bind(10), bind(12), bind(15));
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.count());
  }

  @Test
  public void intRangeCheckGenerate() {
    final Var<Integer> Q = intVar("Q");
    final Object goal = new IntRange(bind(10), Q, bind(15));
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(5, listener.count());
  }


  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

  private LocalSolutionListener solve(Object goal) {
    final Object normalized = TermApi.normalize(goal);
    final LocalSolutionListener solutionListener = new LocalSolutionListener(normalized);
    solver.solveGoal(normalized, solutionListener);
    return solutionListener;
  }


  protected String marshall(Iterable<Object> terms) {
    ArrayList<String> marshalled = new ArrayList<String>();
    for (final Object term : terms) {
      marshalled.add(term.toString());
    }
    return marshalled.toString();
  }


  private class LocalSolutionListener extends ExtractingSolutionListener {

    public LocalSolutionListener(Object goal) {
      super(goal);
    }

    @Override
    public Integer onSolution(UnifyContext currentVars) {
      return super.onSolution(currentVars);
    }
  }

  // ---------------------------------------------------------------------------
  // Low-lever solution counting assertions just using a COuntingSolutionListener
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
      assertEquals("Solving goalText \"" + term + '"', nbr, listener.count());
    }
  }

}