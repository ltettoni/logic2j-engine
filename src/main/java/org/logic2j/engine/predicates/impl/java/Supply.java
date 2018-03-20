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

package org.logic2j.engine.predicates.impl.java;

import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.solver.holder.BindingVar;
import org.logic2j.engine.unify.UnifyContext;

import java.util.Arrays;
import java.util.List;

import static org.logic2j.engine.solver.Continuation.CONTINUE;

/**
 * Supply input data from {@link BindingVar}s into their corresponding variables.
 * The cross-product is generated.
 * For example if {@link BindingVar} S initially defines values "A", "B", and {@link BindingVar} Q
 * initially defines values 1,2,3, then starting a resolution with Supply(S, Q) will emit bindings for
 * variables (S, Q) as (A, 1), (A, 2), (A, 3), (B, 1), (B, 2), (B, 3).
 * On the contrary, invoking Supply(Q, S) will reverse the cross-product.
 */
public class Supply extends FOPredicate {

  private final BindingVar<?>[] bindingVars;
  private List<?>[] data;

  public Supply(BindingVar<?>... vars) {
    super("supplyN", (Object[]) vars);
    this.bindingVars = vars;
    this.data = null;
    // Do nothing in constructor, lazy initialization will occur later
  }

  private void ensureInit() {
    if (this.data == null) {
      synchronized (this) {
        if (this.data == null) {
          final BindingVar[] boundVars = Arrays.stream(bindingVars).filter(BindingVar::isBound).toArray(BindingVar[]::new);
          final int nbVars = boundVars.length;
          this.data = new List<?>[nbVars];
          // Load data (only once) into memory
          for (int i = 0; i < nbVars; i++) {
            final BindingVar var = boundVars[i];
            this.data[i] = var.toList();
          }
        }
      }
    }
  }


  @Override
  public Integer predicateLogic(UnifyContext currentVars) {
    ensureInit();
    notifyFromVar(0, currentVars);
    return CONTINUE;
  }

  private UnifyContext notifyFromVar(int ivar, UnifyContext currentVars) {
    if (ivar >= this.bindingVars.length) {
      final Integer cont = currentVars.getSolutionListener().onSolution(currentVars);
      //      if (cont != Continuation.CONTINUE) {
      //        throw new SolverException(this + " is unable to bind " + var + " to value " + value);
      //      }
      return currentVars;
    }
    final Var var = this.bindingVars[ivar];
    final List<?> data = this.data[ivar];
    final int nbValues = data.size();
    for (int j = 0; j < nbValues; j++) {
      final Object value = data.get(j);
      final UnifyContext afterUnification = currentVars.unify(var, value);
      final boolean couldUnifySomething = afterUnification != null;
      if (!couldUnifySomething) {
        throw new SolverException(this + " is unable to bind " + var + " to value " + value);
      }

      notifyFromVar(ivar + 1, afterUnification);
    }
    return currentVars;
  }
}
