package org.logic2j.predsolver.solver;

import org.junit.Test;
import org.logic2j.predsolver.solver.holder.BindingVar;
import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.predicates.Even;
import org.logic2j.predsolver.predicates.Odd;
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
  public void readFromBoundVar() {
    final MyDTO dto = new MyDTO();
    dto.setValues(IntStream.range(1, 20).boxed().collect(Collectors.toList()));
    //
    final BindingVar<Integer> Q = new BindingVar<Integer>("Q", dto.getValues());
    final Term goal = new Even(Q);
    final List<Object> list = solver.solve(goal).var("Q").list();
    assertThat(list.toString(), is("[2, 4, 6, 8]"));
  }

//  @Test
//  public void checkValues() {
//    final MyDTO dto = new MyDTO();
//    dto.setValues(IntStream.range(1, 10).boxed().collect(Collectors.toList()));
//    //
//    final Var<Integer> Q = new Var<>("Q");
//    final Term goal1 = new Digit(Q);
//    final Term goal2 = new CollectionBinder<Integer, List<Integer>>(dto::getValues, Q, dto::setValues);
//    final List<Object> list = solver.solve(goal1, goal2).var("Q").list();
//    assertThat(list.toString(), is("[1, 2, 3, 4, 5, 6, 7, 8, 9]"));
//  }

  @Test
  public void writeVariableIntoJava() {
    final MyDTO dto = new MyDTO();
    //
    final BindingVar<Integer> Q = new BindingVar<Integer>("Q");
    final BindingVar<Integer> R = new BindingVar<Integer>("R");
    final Term goal = new Even(Q);
    final BindingVar[] boundVars = solver.solve(goal, new Odd(R)).boundVariables();
    logger.info("Result: {}", Q);
    logger.info("Result: {}", R);
//    final List<Object> list = (List<Object>) nbr;
    // assertThat(list.toString(), is("[2, 4, 6, 8]"));
  }

  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

}