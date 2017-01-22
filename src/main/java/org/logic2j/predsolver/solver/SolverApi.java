package org.logic2j.predsolver.solver;

import org.logic2j.predsolver.model.TermApi;
import org.logic2j.predsolver.solver.holder.GoalHolder;

/**
 * Higher level API.
 */
public class SolverApi extends Solver {
  public GoalHolder solve(Object goal) {
    final Object normalized = TermApi.normalize(goal);
    return new GoalHolder(this, goal);
  }
}
