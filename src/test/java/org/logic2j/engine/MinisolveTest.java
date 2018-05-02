/*
 * logic2j - "Bring Logic to your Java" - Copyright (c) 2018 Laurent.Tettoni@gmail.com
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

package org.logic2j.engine;

import org.junit.Test;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.Even;
import org.logic2j.engine.predicates.IntRange;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.predicates.internal.And;
import org.logic2j.engine.predicates.internal.Cut;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.predicates.Predicates.and;
import static org.logic2j.engine.solver.Continuation.CONTINUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MinisolveTest {
  private static final Logger logger = LoggerFactory.getLogger(MinisolveTest.class);

  @Test
  public void unifyAndNotify_single_multiple() {
    final SolutionListener listen = mock(SolutionListener.class);

    final UnifyContext initial = new UnifyContext(null, listen);
    final int cont = initial.unifyAndNotify(3, bind(1,2,3,4));
    assertThat(cont).isEqualTo(CONTINUE);
    verify(listen).onSolution(any());
  }

  @Test
  public void unifyAndNotify_multiple_multiple() {
    final SolutionListener listen = mock(SolutionListener.class);

    final UnifyContext initial = new UnifyContext(null, listen);
    final int cont = initial.unifyAndNotify(bind(1,2,3,4,5,6), bind(0,8,3,2,9,4));
    assertThat(cont).isEqualTo(CONTINUE);
    verify(listen, times(3)).onSolution(any());
  }

  @Test
  public void unifyAndNotify_var_multiple() {
    final SolutionListener listen = mock(SolutionListener.class);

    final UnifyContext initial = new UnifyContext(null, listen);
    final Var<Integer> X = intVar("X");
    termApi().normalize(X);
    final int cont = initial.unifyAndNotify(X, bind(1,2,3,4));
    assertThat(cont).isEqualTo(CONTINUE);
    verify(listen, times(4)).onSolution(any());
  }

  @Test
  public void evenGenerateOnConstant() {
    final FOPredicate goal = new Even(bind(1,2,3,4));
    final UnifyContext unifyContext = new UnifyContext(null, reportSolution());
    goal.predicateLogic(unifyContext);
  }


  @Test
  public void evenGenerateOnVar() {
    final Var<Integer> X = intVar("X");
    final FOPredicate goal = new Even(X);
    termApi().normalize(goal);
    final UnifyContext unifyContext = new UnifyContext(null, reportVar(X));
    goal.predicateLogic(unifyContext);
  }

  @Test
  public void evenAndOddGenerateOnVar() {
    final Var<Integer> X = intVar("X");
    final And goal = and(new Even(X), new IntRange(3,X,12), new Cut());
    termApi().normalize(goal);
    final UnifyContext unifyContext = new UnifyContext(null, reportVar(X));
    goal.newPredicateLogic(unifyContext, 1);
  }

  private SolutionListener reportSolution() {
    return currentVars -> { logger.info("Solution found"); return CONTINUE; };
  }

  private SolutionListener reportVar(Var<?> var) {
    return currentVars -> { logger.info("Solution found: {}", currentVars.reify(var)); return CONTINUE; };
  }
}
