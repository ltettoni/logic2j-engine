package org.logic2j.predsolver.solver;

import org.junit.Test;
import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.Even;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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
    final Term goal = and(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).exists(), is(false));
  }

  @Test
  public void exists2() {
    final Var<Integer> Q = new Var<>("Q");
    final Term goal = or(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).exists(), is(true));
  }

  @Test
  public void count0() {
    final Var<Integer> Q = new Var<>("Q");
    final Term goal = and(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).count(), is(0L));
  }

  @Test
  public void count2() {
    final Var<Integer> Q = new Var<>("Q");
    final Term goal = or(eq(Q, 11), eq(Q, 12));
    assertThat(solver.solve(goal).count(), is(2L));
  }


  @Test
  public void wholeSolution() {
    final Var<Integer> Q = new Var<>("Q");
    final Term goal = new Even(Q);
    final List<Object> list = solver.solve(goal).solution().list();
    assertThat(list.toString(), is("[even(0), even(2), even(4), even(6), even(8)]"));
  }

  @Test
  public void var() {
    final Var<Integer> Q = new Var<>("Q");
    final Term goal = new Even(Q);
    final List<Object> list = solver.solve(goal).var("Q").list();
    assertThat(list.toString(), is("[0, 2, 4, 6, 8]"));
  }

  @Test
  public void vars() {
    final Var<Integer> Q = new Var<>("Q");
    final Term goal = new Even(Q);
    final List<Map<Var<?>, Object>> list = solver.solve(goal).vars().list();
    assertThat(list.toString(), is("[{Q=0}, {Q=2}, {Q=4}, {Q=6}, {Q=8}]"));
  }


  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

}