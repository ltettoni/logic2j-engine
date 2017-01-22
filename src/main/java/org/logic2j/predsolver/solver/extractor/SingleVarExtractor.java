/*
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
package org.logic2j.predsolver.solver.extractor;

import org.logic2j.predsolver.exception.MissingSolutionException;
import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.model.TermApi;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link SolutionExtractor} that will extract the individual
 * values of a single Var or of a single "goal" Term, and possibly convert them to a desired class.
 */
public class SingleVarExtractor<T> implements SolutionExtractor<T> {
    private static final Logger logger = LoggerFactory.getLogger(SingleVarExtractor.class);

    private final Object goal;

    /**
     * The variable whose value is to extract.
     */
    private final Var<?> var;

    /**
     * The target class, or Object if no conversion is asked.
     */
    private Class<? extends T> targetClass;

    /**
     * Create a {@link SolutionListener} that will enumerate
     * solutions up to theMaxCount before aborting by "user request". We will usually
     * supply 1 or 2, see derived classes.
     */
    public SingleVarExtractor(Object goal, String varName, Class<? extends T> desiredTypeOfResult) {
        this.goal = goal;
        final Var<?> found = TermApi.findVar(goal, varName);
        if (found == null) {
            throw new MissingSolutionException("No var named \"" + varName + "\" in term \"" + goal + '"');
        }
        this.var = found;
        this.targetClass = desiredTypeOfResult;
    }

    @Override
    public T extractSolution(UnifyContext currentVars) {
        Object reifiedValue;
        if (var == Var.WHOLE_SOLUTION_VAR) {
            reifiedValue = currentVars.reify(goal);
            // No need to convert values it will be Struct
        } else {
            reifiedValue = currentVars.reify(var);
            if (logger.isDebugEnabled() && reifiedValue instanceof Term && !targetClass.isAssignableFrom(reifiedValue.getClass())) {
                logger.debug("Will convert solution from {} to {}", reifiedValue.getClass(), this.targetClass);
            }
        }
        return (T) reifiedValue;
    }

}