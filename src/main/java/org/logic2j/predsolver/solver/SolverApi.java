package org.logic2j.predsolver.solver;

import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.model.TermApi;
import org.logic2j.predsolver.solver.holder.GoalHolder;

import static org.logic2j.predsolver.predicates.Predicates.and;

/**
 * Higher level API.
 */
public class SolverApi extends Solver {
  public GoalHolder solve(Term... goals) {
    final Object effectiveGoal;
    if (goals.length==1) {
      effectiveGoal = goals[0];
    } else {
      effectiveGoal = and(goals);
    }
    final Object normalized = TermApi.normalize(effectiveGoal);
    return new GoalHolder(this, normalized);
  }
}
