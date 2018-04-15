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

package org.logic2j.engine.predicates.internal;

import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.predicates.external.RDBCompatiblePredicate;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.solver.Solver;
import org.logic2j.engine.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logical OR.
 * The implementation is hard-coded in the Solver, hence we do not provide it here.
 */
public class Or extends SolverPredicate implements RDBCompatiblePredicate {
  private static final Logger logger = LoggerFactory.getLogger(Or.class);

  public Or(Term... disjunctions) {
    super(FUNCTOR_SEMICOLON, (Object[]) disjunctions);
  }

  @Override
  public int predicateLogic(UnifyContext currentVars, int cutLevel) {
    return orLogic(this, currentVars, cutLevel);
  }

  /*
   * This is the Java implementation of N-arity OR
   * We can also implement a binary OR directly in Prolog, see note re. processing of OR in CoreLibrary.pro
   */
  public static int orLogic(Struct goalStruct, UnifyContext currentVars, int cutLevel) {
    final int arity = goalStruct.getArity();
    final Solver solver = currentVars.getSolver();
    for (int i = 0; i < arity; i++) {
      // Solve all the elements of the "OR", in sequence.
      // For a binary OR, this means solving the left-hand-side and then the right-hand-side
      if (logger.isDebugEnabled()) {
        logger.debug("Handling OR, element={} of {}", i, goalStruct);
      }
      final int result = solver.solveInternalRecursive(goalStruct.getArg(i), currentVars, cutLevel);
      if (result != Continuation.CONTINUE) {
        break;
      }
    }
    return Continuation.CONTINUE;
  }
}
