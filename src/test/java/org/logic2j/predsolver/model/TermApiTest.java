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

import org.junit.Test;
import org.logic2j.predsolver.exception.InvalidTermException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Low-level tests of the {@link TermApi} facade.
 */
public class TermApiTest {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TermApiTest.class);

    @Test
    public void placeholderToReproduceError() {
        //
    }


    @Test
    public void structurallyEquals() {
        // Vars are never structurally equal ...
        assertFalse(new Var<Object>("X").structurallyEquals(new Var<Object>("Y")));
        final Var<?> x1 = new Var<Object>("X");
        final Var<?> x2 = new Var<Object>("X");
        // ... even when they have the same name
        assertFalse(x1.structurallyEquals(x2));
        final Struct s = new Struct("s", x1, x2);
        assertFalse(TermApi.structurallyEquals(s.getArg(0), s.getArg(1)));
        // After factorization, the 2 X will be same
        final Struct s2 = (Struct) TermApi.factorize(s);
        assertNotSame(s, s2);
        assertFalse(s.structurallyEquals(s2));
        assertTrue(TermApi.structurallyEquals(s2.getArg(0), s2.getArg(1)));
    }


    @Test
    public void collectTerms1() {
        Term term;
        Var<?> X = new Var<>("X");
        //
        term = new Struct("p", X, 2);
        logger.info("Flat terms: {}", TermApi.collectTerms(term));
    }

    @Test
    public void collectTerms2() {
        Term term;
        //
        term = new Struct("a", new Struct("b"), "c");
        logger.info("Flat terms: {}", TermApi.collectTerms(term));
    }

    @Test
    public void collectTerms3() {
        Term term;
        Var<?> X = new Var<>("X");
        Var<?> Y = new Var<>("Y");
        //
        term = new Struct(Struct.FUNCTOR_CLAUSE, new Struct("a", new Struct("p", X, Y)), new  Struct("p", X, Y));
        logger.info("Flat terms: {}", TermApi.collectTerms(term));
    }

    @Test
    public void collectTerms4() {
        Term term;
        Var<?> X = new Var<>("X");
        Var<?> Y = new Var<>("Y");
        final Term clause = new Struct(Struct.FUNCTOR_CLAUSE, new Struct("a", new  Struct("p", X, Y)), new  Struct("p", X, Y));
        logger.info("Flat terms of original {}", TermApi.collectTerms(clause));
        final Object t2 = TermApi.normalize(clause);
        logger.info("Found {} bindings", ((Struct) t2).getIndex());
        assertEquals(2, ((Struct) t2).getIndex());
        logger.info("Flat terms of copy     {}", TermApi.collectTerms(t2));
        assertEquals(clause.toString(), t2.toString());
    }

    @Test
    public void assignIndexes() {
        int nbVars;
        nbVars = TermApi.assignIndexes(new Struct("f"), 0);
        assertEquals(0, nbVars);
        nbVars = TermApi.assignIndexes(new Var<Object>("X"), 0);
        assertEquals(1, nbVars);
        nbVars = TermApi.assignIndexes(Var.ANONYMOUS_VAR, 0);
        assertEquals(0, nbVars);
        //
        nbVars = TermApi.assignIndexes(Long.valueOf(2), 0);
        assertEquals(0, nbVars);
        nbVars = TermApi.assignIndexes(Double.valueOf(1.1), 0);
        assertEquals(0, nbVars);
    }



    @Test(expected=InvalidTermException.class)
    public void functorFromSignatureFails() throws Exception {
        TermApi.functorFromSignature("toto4");
    }


    @Test
    public void functorFromSignature1() throws Exception {
        assertEquals("toto", TermApi.functorFromSignature("toto/4"));
    }



    @Test(expected=InvalidTermException.class)
    public void arityFromSignatureFails() throws Exception {
        TermApi.arityFromSignature("toto4");
    }

    @Test
    public void arityFromSignature1() throws Exception {
        assertEquals(4, TermApi.arityFromSignature("toto/4"));
    }

    @Test
    public void quoteIfNeeded() throws Exception {
        assertNull(TermApi.quoteIfNeeded(null));
        assertEquals("''", TermApi.quoteIfNeeded("").toString());
        assertEquals("' '", TermApi.quoteIfNeeded(" ").toString());
        assertEquals("ab", TermApi.quoteIfNeeded("ab").toString());
        assertEquals("'Ab'", TermApi.quoteIfNeeded("Ab").toString());
        assertEquals("'it''s'", TermApi.quoteIfNeeded("it's").toString());
        assertEquals("'a''''b'", TermApi.quoteIfNeeded("a''b").toString());
        assertEquals("'''that'''", TermApi.quoteIfNeeded("'that'").toString());
    }
}
