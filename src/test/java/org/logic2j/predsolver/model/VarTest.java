package org.logic2j.predsolver.model;

import org.junit.Test;
import org.logic2j.predsolver.exception.InvalidTermException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.logic2j.predsolver.model.Var.anyVar;

public class VarTest {

    @Test
    public void constructorValid() {
        final Var<?> v1 = anyVar("X");
        assertSame("X", v1.getName());
        assertEquals(Term.NO_INDEX, v1.getIndex());
    }

    @Test(expected = InvalidTermException.class)
    public void constructorNull() {
        anyVar((String) null);
    }

    @Test(expected = InvalidTermException.class)
    public void constructorEmpty() {
        anyVar("");
    }

    @Test(expected = InvalidTermException.class)
    public void constructorCannotInstantiateAnonymous() {
        anyVar("_");
    }


    @Test
    public void constructorWithCharSequence() {
        final Var<?> v1 = anyVar(new StringBuilder("X"));
        assertSame("X", v1.getName());
        assertEquals(Term.NO_INDEX, v1.getIndex());
    }


    @Test
    public void idempotence() {
        final Var<?> v1 = anyVar("X");
        assertEquals(v1, v1);
    }


    @Test
    public void equality() {
        final Var<?> v1 = anyVar("X");
        final Var<?> v2 = anyVar("X");
        assertNotSame(v1, v2);
        assertEquals(v1, v2);
        assertEquals(v2, v1);
    }


    @Test
    public void lowerCaseIsValid() {
        final Var<?> v1 = anyVar("lowercase");
        assertSame("lowercase", v1.getName());
        assertEquals(Term.NO_INDEX, v1.getIndex());
    }


    @Test(expected = InvalidTermException.class)
    public void cannotCloneAnonymous() {
        Var.copy(Var.ANONYMOUS_VAR);
    }

    @Test
    public void isAnonymousTrue() {
        assertTrue(Var.ANONYMOUS_VAR.isAnonymous());
    }

    @Test
    public void isAnonymousFalse() {
        assertFalse(anyVar("X").isAnonymous());
    }




}
