package org.logic2j.predsolver.solver;

import org.junit.Test;
import org.logic2j.predsolver.model.Struct;
import org.logic2j.predsolver.model.TermApi;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.solver.listener.CountingSolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.logic2j.predsolver.predicates.Predicates.and;
import static org.logic2j.predsolver.predicates.Predicates.fail;
import static org.logic2j.predsolver.predicates.Predicates.or;
import static org.logic2j.predsolver.predicates.Predicates.ttrue;

public class SolverTest {
  private static final Logger logger = LoggerFactory.getLogger(SolverTest.class);

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

//  @Test
//  public void corePrimitivesThatYieldUniqueSolution() {
//    final String[] SINGLE_SOLUTION_GOALS = new String[] { //
//        "true", //
//        "true, true", //
//        "true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true", //
//        "!", //
//        "!, !", //
//    };
//    countOneSolution(SINGLE_SOLUTION_GOALS);
//  }
//
//
//  @Test
//  public void corePrimitivesThatYieldNoSolution() {
//    final String[] NO_SOLUTION_GOALS = new String[] { //
//        "fail", //
//        "fail, fail", //
//        "fail, fail, fail, fail, fail, fail, fail, fail, fail, fail, fail, fail, fail, fail, fail, fail, fail", //
//        "true, fail", //
//        "fail, true", //
//        "true, true, fail", //
//        "true, fail, !", //
//    };
//    countNoSolution(NO_SOLUTION_GOALS);
//  }
//
//
//  /**
//   * This is a special feature of logic2j: AND with any arity
//   */
//  @Test
//  public void nonBinaryAnd() {
//    loadTheoryFromTestResourcesDir("test-functional.pro");
//    final String[] SINGLE_SOLUTION_GOALS = new String[] { //
//        "','(true)", //
//        "','(true, true)", //
//        "','(true, !, true)", //
//    };
//    countOneSolution(SINGLE_SOLUTION_GOALS);
//  }
//
//
//  @Test
//  public void or() {
//    loadTheoryFromTestResourcesDir("test-functional.pro");
//    countNSolutions(2, "';'(true, true)");
//    countNSolutions(2, "true; true");
//    //
//    countNSolutions(3, "true; true; true");
//    //
//    GoalHolder solutions;
//    solutions = this.prolog.solve("X=a; X=b; X=c");
//    assertEquals("[a, b, c]", solutions.var("X").list().toString());
//  }
//
//
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
//
//  @Test
//  public void not() {
//    // Surprisingly enough, the operator \+ means "not provable".
//    uniqueSolution("not(fail)", "\\+(fail)");
//    nSolutions(0, "not(true)", "\\+(true)");
//  }
//
//
//
//
//
//
//
//
//  // ---------------------------------------------------------------------------
//  // Basic unification
//  // ---------------------------------------------------------------------------
//
//
//  @Test
//  public void unifyLiteralsNoSolution() {
//    final Object goal = unmarshall("a=b");
//    final long nbSolutions = solve(goal).getCounter();
//    assertEquals(0, nbSolutions);
//  }
//
//
//  @Test
//  public void unifyLiteralsOneSolution() {
//    final Object goal = unmarshall("c=c");
//    final long nbSolutions = solve(goal).getCounter();
//    assertEquals(1, nbSolutions);
//  }
//
//
//  @Test
//  public void unifyAnonymousToAnonymous() {
//    final Object goal = unmarshall("_=_");
//    final long nbSolutions = solve(goal).getCounter();
//    assertEquals(1, nbSolutions);
//  }
//
//
//  @Test
//  public void unifyVarToLiteral() {
//    final Object goal = unmarshall("Q=d");
//    final ExtractingSolutionListener listener = solve(goal);
//    assertEquals(1, listener.getCounter());
//    assertEquals("[Q]", listener.getVariables().toString());
//    assertEquals("[d = d]", marshall(listener.getValues(".")));
//    assertEquals("[d]", marshall(listener.getValues("Q")));
//  }
//
//  @Test
//  public void unifyVarToAnonymous() {
//    final Object goal = unmarshall("Q=_");
//    final ExtractingSolutionListener listener = solve(goal);
//    assertEquals(1, listener.getCounter());
//    assertEquals("[Q]", listener.getVariables().toString());
//    assertEquals("[_ = _]", marshall(listener.getValues(".")));
//    assertEquals("[_]", marshall(listener.getValues("Q")));
//  }
//
//
//  @Test
//  public void unifyVarToVar() {
//    final Object goal = unmarshall("Q=Z");
//    final ExtractingSolutionListener listener = solve(goal);
//    assertEquals(1, listener.getCounter());
//    assertEquals("[., Q, Z]", listener.getVarNames().toString());
//    assertEquals("[Q = Q]", marshall(listener.getValues(".")));
//    assertEquals("[Q]", marshall(listener.getValues("Q")));
//    assertEquals("[Q]", marshall(listener.getValues("Z")));
//  }


  private LoggingSolutionListener solve(Object goal) {
    final Object normalized = TermApi.normalize(goal);
    final LoggingSolutionListener theSolutionListener = new LoggingSolutionListener();
    final Integer result = new Solver().solveGoal(normalized, theSolutionListener);
    return theSolutionListener;
  }


  private class LoggingSolutionListener extends CountingSolutionListener {

    @Override
    public Integer onSolution(UnifyContext currentVars) {
      return super.onSolution(currentVars);
    }
  }


}