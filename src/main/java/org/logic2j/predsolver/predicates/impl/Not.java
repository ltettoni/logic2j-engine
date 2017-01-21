package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.Solver;
import org.logic2j.predsolver.solver.listener.FirstSolutionListener;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Unification operator "=".
 */
public class Not extends Predicate {
  private Solver solver;

  public Not(Solver solver, Term theGoal) {
    super("\\+", theGoal);
    this.solver = solver;
  }

  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {

    final FirstSolutionListener goalListener = new FirstSolutionListener();

    this.solver.solveGoal(getArg(0), currentVars, goalListener);
    final Integer continuation;
    if (goalListener.hasSolution()) {
      // The goal provided at least one solution (and we stopped there, see doc of FirstSolutionListener)
      // Do NOT propagate a solution to our own listener, and continue normal evaluation.
      continuation = Continuation.CONTINUE;
    } else {
      // Not found - notify a solution (that's the purpose of not/1 !)
      continuation = theListener.onSolution(currentVars);
    }
    return continuation;
  }


}