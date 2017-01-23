package org.logic2j.predsolver.solver;

import org.logic2j.predsolver.solver.holder.BindingVar;
import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.model.TermApi;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.impl.Supply;
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

    final Var<?>[] vars = TermApi.distinctVars(and(goalList.toArray(new Term[goalList.size()])));
    final BindingVar[] bindingVars = Arrays.stream(vars).filter(BindingVar.class::isInstance).map(BindingVar.class::cast).toArray(BindingVar[]::new);

    for (BindingVar bv : bindingVars) {
      if (bv.iterator() != null) {
        goalList.add(0, new Supply(bv.iterator(), bv));
      }
    }
    final Term effective = goalList.size() == 1 ? goalList.get(0) : and(goalList.toArray(new Term[goalList.size()]));
    final Object normalized = TermApi.normalize(effective);
    final BindingVar[] freeBindingVars = Arrays.stream(bindingVars).filter(bv -> !bv.isBound()).toArray(BindingVar[]::new);
    return new GoalHolder(this, normalized, freeBindingVars);
  }
}
