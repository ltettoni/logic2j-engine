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


import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.unify.UnifyContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class UnifyContextIterator implements Iterator<UnifyContext> {

    private final Var<?> var;

    private final Collection<?> values;

    private final UnifyContext currentVars;

    private final Iterator<?> iter;

    public UnifyContextIterator(UnifyContext currentVars, Var<?> theVar, Collection<?> values) {
        this.var = theVar;
        this.values = values;
        this.currentVars = currentVars;
        this.iter = this.values.iterator();
    }


    public UnifyContextIterator(UnifyContext currentVars, Iterator<UnifyContext> multiLHS, Iterator<UnifyContext> multiRHS) {
        if (! (multiLHS instanceof UnifyContextIterator)) {
            throw new UnsupportedOperationException("Left argument must be instanceof ListMultiResult");
        }
        if (! (multiRHS instanceof UnifyContextIterator)) {
            throw new UnsupportedOperationException("Right argument must be instanceof ListMultiResult");
        }
        final UnifyContextIterator left = (UnifyContextIterator)multiLHS;
        final UnifyContextIterator right = (UnifyContextIterator)multiRHS;
        if (left.var != right.var) {
            throw new UnsupportedOperationException("Must have same var to combine");
        }
        this.var = left.var;
        this.values = new ArrayList<>(left.values);
        this.values.retainAll(right.values);
        this.currentVars = currentVars;
        this.iter = this.values.iterator();
    }


    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public UnifyContext next() {
        final Object next = iter.next();
        final UnifyContext after = currentVars.unify(var, next);
        return after;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove item from this iterator");
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + this.values;
    }

}