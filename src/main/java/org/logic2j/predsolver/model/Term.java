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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

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
    static final Logger logger = LoggerFactory.getLogger(Term.class);
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
    // Accessors
    // ---------------------------------------------------------------------------

    public int getIndex() {
        return this.index;
    }




}
