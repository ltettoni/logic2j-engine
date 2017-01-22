package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.solver.Solver;
import org.logic2j.predsolver.solver.listener.ExistsSolutionListener;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * Unification operator "=".
 */
public class Not extends FOPredicate {
  public static final String FUNCTOR = "\\+";
  private Solver solver;

  public Not(Solver solver, Term theGoal) {
    super(FUNCTOR, theGoal);
    this.solver = solver;
  }

  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {

    // Solve against a minimal SolutionListener just interested on the first solution
    final ExistsSolutionListener goalListener = new ExistsSolutionListener();
    this.solver.solveGoal(getArg(0), goalListener, currentVars);

    final boolean doesNotExist = !goalListener.exists();
    return notifySolutionIf(doesNotExist, theListener, currentVars);
  }


}
