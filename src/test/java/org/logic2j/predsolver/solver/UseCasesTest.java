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

package org.logic2j.predsolver.solver;

import org.junit.Test;
import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.Digit;
import org.logic2j.predsolver.predicates.Even;
import org.logic2j.predsolver.predicates.Odd;
import org.logic2j.predsolver.predicates.impl.FOFilter;
import org.logic2j.predsolver.predicates.impl.FOMap;
import org.logic2j.predsolver.solver.holder.BindingVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.predsolver.model.Var.intVar;
import static org.logic2j.predsolver.solver.holder.BindingVar.intBVar;

public class UseCasesTest {
  private static final Logger logger = LoggerFactory.getLogger(UseCasesTest.class);
  private SolverApi solver = new SolverApi();

  @Test
  public void readFromBoundVar() {
    final MyDTO dto = new MyDTO();
    dto.setValues(IntStream.range(1, 20).boxed().collect(Collectors.toList()));
    //
    final BindingVar<Integer> Q = intBVar("Q", dto.getValues());
    final Term goal = new Even(Q);
    final List<Object> list = solver.solve(goal).var("Q").list();
    assertThat(list.toString(), is("[2, 4, 6, 8]"));
  }


  @Test
  public void writeVariableIntoJava() {
    final MyDTO dto = new MyDTO();
    //
    final BindingVar<Integer> Q = intBVar("Q");
    final BindingVar<Integer> R = intBVar("R");
    final Term goal = new Even(Q);
    final BindingVar[] boundVars = solver.solve(goal, new Odd(R)).boundVariables();
    logger.info("Result: {}", Q);
    logger.info("Result: {}", R);
    //    final List<Object> list = (List<Object>) nbr;
    // assertThat(list.toString(), is("[2, 4, 6, 8]"));
  }

  @Test
  public void usingPlainJavaPredicate() {
    final Var<Integer> Q = intVar("Q");
    final List<Object> list = solver.solve(new Digit(Q), new Odd(Q), new FOFilter<>(Q, i -> i != 5)).var("Q").list();
    logger.info("Result: {}", list);
  }

  @Test
  public void usingPlainJavaFunction() {
    final Var<Integer> Q = intVar("Q");
    final Var<Integer> R = intVar("R");
    final List<Object> list = solver.solve(new Digit(Q), new FOMap<>(Q, x -> x*x, R)).var("R").list();
    logger.info("Result: {}", list);
  }

  @Test
  public void useCaseForRetrievalApi() {


  }

  // --------------------------------------------------------------------------
  // Support methods
  // --------------------------------------------------------------------------

}