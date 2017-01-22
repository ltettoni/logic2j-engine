package org.logic2j.predsolver.solver;

import org.junit.Test;
import org.logic2j.predsolver.model.BoundVar;
import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.Digit;
import org.logic2j.predsolver.predicates.Even;
import org.logic2j.predsolver.predicates.impl.CollectionBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UseCasesTest {
  private static final Logger logger = LoggerFactory.getLogger(UseCasesTest.class);
  private SolverApi solver = new SolverApi();

  @Test
  public void readJavaIntoVariable() throws Exception {
    final MyDTO dto = new MyDTO();
    dto.setValues(IntStream.range(1, 20).boxed().collect(Collectors.toList()));
    //
    final Var<Integer> Q = new Var<>("Q");
    final Term goal = new CollectionBinder<Integer, List<Integer>>(dto::getValues, Q, dto::setValues);
    final List<Object> list = solver.solve(goal).var("Q").list();
    assertThat(list.toString(), is("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]"));
  }

  @Test
  public void checkValues() throws Exception {
    final MyDTO dto = new MyDTO();
    dto.setValues(IntStream.range(1, 10).boxed().collect(Collectors.toList()));
    //
    final Var<Integer> Q = new Var<>("Q");
    final Term goal1 = new Digit(Q);
    final Term goal2 = new CollectionBinder<Integer, List<Integer>>(dto::getValues, Q, dto::setValues);
    final List<Object> list = solver.solve(goal1, goal2).var("Q").list();
    assertThat(list.toString(), is("[1, 2, 3, 4, 5, 6, 7, 8, 9]"));
  }

  @Test
  public void writeVariableIntoJava() throws Exception {
    final MyDTO dto = new MyDTO();
    //
    final Var<Integer> Q = new Var<>("Q");
    final Term goal1 = new Digit(Q);
    final Term goal2 = new CollectionBinder<Integer, List<Integer>>(dto::getValues, Q, dto::setValues);
    final List<Object> list = solver.solve(goal1, goal2).var("Q").list();
    assertThat(list.toString(), is("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]"));
  }



  @Test
  public void readFromBoundVar() throws Exception {
    final MyDTO dto = new MyDTO();
    dto.setValues(IntStream.range(1, 20).boxed().collect(Collectors.toList()));
    //
    final BoundVar<Integer> Q = new BoundVar<Integer>("Q", dto.getValues());
    final Term goal = new Even(Q);
    final List<Object> list = solver.solve(goal).var("Q").list();
    assertThat(list.toString(), is("[2, 4, 6, 8]"));
  }

//  @Test
//  public void checkValues() throws Exception {
//    final MyDTO dto = new MyDTO();
//    dto.setValues(IntStream.range(1, 10).boxed().collect(Collectors.toList()));
//    //
//    final Var<Integer> Q = new Var<>("Q");
//    final Term goal1 = new Digit(Q);
//    final Term goal2 = new CollectionBinder<Integer, List<Integer>>(dto::getValues, Q, dto::setValues);
//    final List<Object> list = solver.solve(goal1, goal2).var("Q").list();
//    assertThat(list.toString(), is("[1, 2, 3, 4, 5, 6, 7, 8, 9]"));
//  }
//
//  @Test
//  public void writeVariableIntoJava() throws Exception {
//    final MyDTO dto = new MyDTO();
//    //
//    final Var<Integer> Q = new Var<>("Q");
//    final Term goal1 = new Digit(Q);
//    final Term goal2 = new CollectionBinder<Integer, List<Integer>>(dto::getValues, Q, dto::setValues);
//    final List<Object> list = solver.solve(goal1, goal2).var("Q").list();
//    assertThat(list.toString(), is("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]"));
//  }

  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

}