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
import org.logic2j.predsolver.solver.listener.multi.MultiResult;
import org.logic2j.predsolver.unify.UnifyContext;

import java.util.Iterator;

public abstract class SolutionListenerBase implements SolutionListener {

    @Override
    public Integer onSolutions(MultiResult multi) {
        final Iterator<UnifyContext> allSolutions = multi;
        while (allSolutions.hasNext()) {
            final UnifyContext next = allSolutions.next();
            final Integer continuation = this.onSolution(next);
            if (continuation != Continuation.CONTINUE) {
                return continuation;
            }
        }
        return Continuation.CONTINUE;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}