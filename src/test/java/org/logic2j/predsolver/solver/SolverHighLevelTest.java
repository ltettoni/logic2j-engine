package org.logic2j.predsolver.solver;

import org.junit.Test;
import org.logic2j.predsolver.model.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.predsolver.predicates.Predicates.and;
import static org.logic2j.predsolver.predicates.Predicates.eq;
import static org.logic2j.predsolver.predicates.Predicates.or;

public class SolverHighLevelTest {
  private static final Logger logger = LoggerFactory.getLogger(SolverHighLevelTest.class);
  private SolverApi solver = new SolverApi();

  @Test
  public void exists0() {
    final Var<Integer> Q = new Var<>("Q");
    final Object goal = and(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).exists(), is(false));
  }

  @Test
  public void exists2() {
    final Var<Integer> Q = new Var<>("Q");
    final Object goal = or(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).exists(), is(true));
  }

  @Test
  public void count0() {
    final Var<Integer> Q = new Var<>("Q");
    final Object goal = and(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).count(), is(0L));
  }

  @Test
  public void count2() {
    final Var<Integer> Q = new Var<>("Q");
    final Object goal = or(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).count(), is(2L));
  }


  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

}