package org.logic2j.predsolver.solver;

import org.junit.Test;
import org.logic2j.predsolver.model.Struct;
import org.logic2j.predsolver.model.TermApi;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.Digit;
import org.logic2j.predsolver.predicates.impl.Not;
import org.logic2j.predsolver.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.logic2j.predsolver.predicates.Predicates.and;
import static org.logic2j.predsolver.predicates.Predicates.anonymous;
import static org.logic2j.predsolver.predicates.Predicates.cut;
import static org.logic2j.predsolver.predicates.Predicates.eq;
import static org.logic2j.predsolver.predicates.Predicates.fail;
import static org.logic2j.predsolver.predicates.Predicates.or;
import static org.logic2j.predsolver.predicates.Predicates.ttrue;

public class SolverTest {
  private static final Logger logger = LoggerFactory.getLogger(SolverTest.class);
  private Solver solver = new Solver();

  // ---------------------------------------------------------------------------
  // Simplest primitives and undefined goal
  // ---------------------------------------------------------------------------

  @Test
  public void primitiveFail() {
    final Object goal = fail;
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(0, nbSolutions);
  }

  @Test
  public void primitiveTrue() {
    final Object goal = ttrue;
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(1, nbSolutions);
  }


  @Test
  public void dataOnlyAtom() {
    final Object goal = new Struct("atom");
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(0, nbSolutions);
  }

  @Test
  public void dataOnlyStructWithParam() {
    final Object goal = new Struct("atom", "p1");
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(0, nbSolutions);
  }

  @Test
  public void dataOnlyStructWithParams() {
    final Object goal = new Struct("atom", "p1", new Struct("p2", "p21", "p22"));
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(0, nbSolutions);
  }

  @Test
  public void dataOnlyStructWithVar() {
    final Var<Object> X = new Var<>("X");
    final Object goal = new Struct("atom", X);
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(0, nbSolutions);
  }

  @Test
  public void dataOnlyStructWithVars() {
    final Var<Object> X = new Var<>("X");
    final Var<Object> Y = new Var<>("Y");
    final Object goal = new Struct("atom", X, Y);
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(0, nbSolutions);
  }


  @Test
  public void primitiveTrueAndTrue() {
    final Object goal = and(ttrue, ttrue);
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(1, nbSolutions);
  }


  @Test
  public void primitiveTrueOrTrue() {
    final Object goal = or(ttrue, ttrue);
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(2, nbSolutions);
  }

  @Test
  public void primitiveCut() {
    final Object goal = cut;
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(1, nbSolutions);
  }



    @Test
    public void orTest() {
      countNSolutions(2, or(ttrue, ttrue));
      //
      countNSolutions(3, or(ttrue, ttrue, ttrue));
      //
      Var<Integer> Q = new Var<>("Q");
      countNSolutions(3, or(eq(Q, 1), eq(Q, 2), eq(Q, 3)));
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
    public void not() {
//      countOneSolution(new Not(solver, fail));
      countNoSolution(new Not(solver, ttrue));
    }



  // ---------------------------------------------------------------------------
  // Basic unification
  // ---------------------------------------------------------------------------


  @Test
  public void unifyLiteralsNoSolution() {
    final Object goal = eq("a", "b");
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(0, nbSolutions);
  }


  @Test
  public void unifyLiteralsOneSolution() {
    final Object goal = eq("c", "c");
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(1, nbSolutions);
  }


  @Test
  public void unifyAnonymousToAnonymous() {
    final Object goal = eq(anonymous, anonymous);
    final long nbSolutions = solve(goal).getCounter();
    assertEquals(1, nbSolutions);
  }


    @Test
    public void unifyVarToLiteral() {
      Var<String> Q = new Var<>("Q");
      final Object goal = eq(Q, "d");
      final long nbSolutions = solve(goal).getCounter();
      assertEquals(1, nbSolutions);
      final ExtractingSolutionListener listener = solve(goal);
      assertEquals(1, listener.getCounter());
      assertEquals("[Q]", listener.getVariables().toString());
      assertEquals("['='(d, d)]", marshall(listener.getValues(".")));
      assertEquals("[d]", marshall(listener.getValues("Q")));
    }


    @Test
    public void unifyVarToAnonymous() {
      Var<String> Q = new Var<>("Q");
      final Object goal = eq(Q, anonymous);
      final ExtractingSolutionListener listener = solve(goal);
      assertEquals(1, listener.getCounter());
      assertEquals("[Q]", listener.getVariables().toString());
      assertEquals("['='(_, _)]", marshall(listener.getValues(".")));
      assertEquals("[_]", marshall(listener.getValues("Q")));
    }


    @Test
    public void unifyVarToVar() {
      Var<String> Q = new Var<>("Q");
      Var<String> Z = new Var<>("Z");
      final Object goal = eq(Q, Z);
      final ExtractingSolutionListener listener = solve(goal);
      assertEquals(1, listener.getCounter());
      assertEquals("[., Q, Z]", listener.getVarNames().toString());
      assertEquals("['='(Q, Q)]", marshall(listener.getValues(".")));
      assertEquals("[Q]", marshall(listener.getValues("Q")));
      assertEquals("[Q]", marshall(listener.getValues("Z")));
    }


  @Test
  public void digitVar() {
    Var<Integer> Q = new Var<>("Q");
    final Object goal = new Digit(Q);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(10, listener.getCounter());
  }


  @Test
  public void digit0() {
    final Object goal = new Digit(0);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.getCounter());
  }

  @Test
  public void digit9() {
    final Object goal = new Digit(9);
    final ExtractingSolutionListener listener = solve(goal);
    assertEquals(1, listener.getCounter());
  }


  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

  private LocalSolutionListener solve(Object goal) {
    final Object normalized = TermApi.normalize(goal);
    final LocalSolutionListener theSolutionListener = new LocalSolutionListener(normalized);
    solver.solveGoal(normalized, theSolutionListener);
    return theSolutionListener;
  }


  protected String marshall(Iterable<Object> terms) {
    ArrayList<String> marshalled = new ArrayList<String>();
    for (Object term : terms) {
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
    for (Object term : theGoals) {
      final LocalSolutionListener listener = solve(term);
      assertEquals("Solving goalText \"" + term + '"', nbr, listener.getCounter());
    }
  }

}