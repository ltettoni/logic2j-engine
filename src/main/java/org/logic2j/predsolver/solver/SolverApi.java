package org.logic2j.predsolver.solver;

import org.logic2j.predsolver.model.BoundVar;
import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.model.TermApi;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.impl.Supply;
import org.logic2j.predsolver.predicates.internal.And;
import org.logic2j.predsolver.solver.holder.GoalHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.logic2j.predsolver.predicates.Predicates.and;

/**
 * Higher level API.
 */
public class SolverApi extends Solver {
  public GoalHolder solve(Term... goals) {
    final List<Term> goalList = Arrays.stream(goals).collect(Collectors.toList());
//    final Object andedGoals;
//    if (goals.length==1) {
//      andedGoals = goals[0];
//    } else {
//      andedGoals = and(goals);
//    }

    final Var<?>[] vars = TermApi.distinctVars(and(goalList.toArray(new Term[goalList.size()])));
    final BoundVar[] boundVars = Arrays.stream(vars).filter(BoundVar.class::isInstance).map(BoundVar.class::cast).toArray(BoundVar[]::new);

    for (BoundVar bv : boundVars) {
      if (bv.iterator() != null) {
        goalList.add(0, new Supply(bv.iterator(), bv));
      }
    }
    final And and = and(goalList.toArray(new Term[goalList.size()]));
    final Object normalized = TermApi.normalize(and);
    return new GoalHolder(this, normalized);
  }
}
