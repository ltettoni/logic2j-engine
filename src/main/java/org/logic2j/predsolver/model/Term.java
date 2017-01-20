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
package org.logic2j.predsolver.model;

import org.logic2j.predsolver.visitor.TermVisitor;

import java.io.Serializable;
import java.util.Collection;

/**
 * Term class is the root abstract class for all Prolog data types.
 * <ul>
 * <li>Normalization: includes initialization of indexes, factorization, and identification of primitive functors</li>
 * </ul>
 *
 * @note Maybe one day we will need a subclass to represent timestamps.
 * @see Var
 */
public abstract class Term implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * A value of index=={@value} (NO_INDEX) means it was not initialized.
     */
    public static final int NO_INDEX = -1;

    /**
     * For {@link Var}s: defines the unique index to the variable.
     * The default value is NO_INDEX.
     * TODO A field must not be public !!!
     */
    public int index = NO_INDEX;

    /**
     * A value of index=={@value} (ANON_INDEX) means this is the anonymous variable.
     */
    public static final int ANON_INDEX = -2;

    // ---------------------------------------------------------------------------
    // TermVisitor
    // ---------------------------------------------------------------------------

    public abstract <T> T accept(TermVisitor<T> theVisitor);


    // ---------------------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------------------

    public int getIndex() {
        return this.index;
    }



    // ---------------------------------------------------------------------------
    // Graph traversal methods, template methods with "protected" scope, user code should use TermApi methods instead.
    // Some traversal are implemented by the Visitor design pattern and the #accept() method
    // ---------------------------------------------------------------------------

    /**
     * Find the first {@link Term} that is either same, or structurally equal to this.
     *
     * @param findWithin
     * @return The {@link Term} found or null when none found.
     */
    protected Object findStructurallyEqualWithin(Collection<Object> findWithin) {
        for (final Object term : findWithin) {
            if (term != this && TermApi.structurallyEquals(term, this)) {
                return term;
            }
        }
        return null;
    }


}
