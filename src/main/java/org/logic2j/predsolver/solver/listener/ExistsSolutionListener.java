package org.logic2j.predsolver.solver.listener;/*
 * logic2j - "Bring Logic to your Java" - Copyright (C) 2011 Laurent.Tettoni@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */


import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.unify.UnifyContext;

/**
 * The simplest {@link SolutionListener} that only checks existence of the first solution, and then aborts execution of subsequent ones.
 * Watch out, due to aborting execution after the first solution, there may be less execution that you might expect. The side-effect
 * is similar to evaluating a function in the middle of logical ANDs: previous results may not necessitate further executions.
 */
public class ExistsSolutionListener implements SolutionListener {
    private boolean atLeastOneSolution = false;

    @Override
    public Integer onSolution(UnifyContext currentVars) {
        // Do NOT relay the solution further, just remember there was one
        this.atLeastOneSolution = true;
        // No need to seek for further solutions. Watch out this means the goal will stop evaluating on first success.
        // Fixme Should rather say the enumeration was cancelled on purpose (optimized like in AND statements)
        return Continuation.USER_ABORT;
    }

    public boolean exists() {
        return atLeastOneSolution;
    }
}
