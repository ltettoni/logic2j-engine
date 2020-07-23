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
import org.logic2j.engine.solver.Solver;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logical AND.
 * The implementation is hard-coded in the Solver, hence we do not provide it here.
 */
public class And extends SolverPredicate implements RDBCompatiblePredicate {
  private static final Logger logger = LoggerFactory.getLogger(And.class);

  public And(Term... conjunctions) {
    super(FUNCTOR_COMMA, (Object[]) conjunctions);
  }

  @Override
  public int predicateLogic(UnifyContext currentVars, int cutLevel) {
    return andLogic(this, currentVars, cutLevel);
  }

  /**
   * Logical AND. Typically the arity=2 since "," is a binary predicate. But in logic2j we allow more, the same code supports both.
   * <p/>
   * Algorithm: for the sequential AND of N goals G1,G2,G3,...,GN, we defined N-1 listeners, and solve G1 against
   * the first listener: all solutions to G1, will be escalated to that listener that handles G2,G3,...,GN
   * Then that listener will solve G2 against the listener for (final G3,...,GN). Finally GN will solve against the
   * "normal" listener received as argument (hence propagating the ANDed solution to our caller).
   * <p/>
   * Note that instantiating all these listeners could be costly - if we found a way to have a cache (eg. storing them
   * at parse-time in Clauses) it could improve performance.
   *
   * @param goal
   * @param currentVars
   * @param cutLevel
   * @return
   */
  public static int andLogic(Struct<?> goal, UnifyContext currentVars, final int cutLevel) {
    final int arity = goal.getArity();

    final SolutionListener[] andingListeners = new SolutionListener[arity];
    // The last listener is the one that called us (typically the one of the application, if this is the outermost "AND")
    andingListeners[arity - 1] = currentVars.getSolutionListener();
    // Allocates N-1 andingListeners, usually this means one.
    // On solution, each will trigger solving of the next term
    final Object[] goalStructArgs = goal.getArgs();
    final Object lhs = goalStructArgs[0];
    final Solver solver = currentVars.getSolver();
    for (int i = 0; i < arity - 1; i++) {
      final int index = i;
      andingListeners[index] = new SolutionListener() {

        @Override
        public int onSolution(UnifyContext currentVars) {
          final int nextIndex = index + 1;
          final Object rhs = goalStructArgs[nextIndex]; // Usually the right-hand-side of a binary ','
          if (logger.isDebugEnabled()) {
            logger.debug("{}: onSolution() called; will now solve rhs={}", this, rhs);
          }
          return solver.solveInternalRecursive(rhs, currentVars.withListener(andingListeners[nextIndex]), cutLevel);
        }

        @Override
        public String toString() {
          return "AND sub-listener to " + lhs;
        }
      };
    }
    // Solve the first goal, redirecting all solutions to the first listener defined above
    if (logger.isDebugEnabled()) {
      logger.debug("Handling AND, arity={}, will now solve lhs={}", arity, currentVars.reify(lhs));
    }
    return solver.solveInternalRecursive(lhs, currentVars.withListener(andingListeners[0]), cutLevel);
  }

}
