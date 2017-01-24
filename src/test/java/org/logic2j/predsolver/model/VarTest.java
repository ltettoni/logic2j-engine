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
    public void constructorValid() throws Exception {
        final Var<?> v1 = anyVar("X");
        assertSame("X", v1.getName());
        assertEquals(Term.NO_INDEX, v1.getIndex());
    }

    @Test(expected = InvalidTermException.class)
    public void constructorNull() throws Exception {
        anyVar((String) null);
    }

    @Test(expected = InvalidTermException.class)
    public void constructorEmpty() throws Exception {
        anyVar("");
    }

    @Test(expected = InvalidTermException.class)
    public void constructorCannotInstantiateAnonymous() throws Exception {
        anyVar("_");
    }


    @Test
    public void constructorWithCharSequence() throws Exception {
        final Var<?> v1 = anyVar(new StringBuilder("X"));
        assertSame("X", v1.getName());
        assertEquals(Term.NO_INDEX, v1.getIndex());
    }


    @Test
    public void idempotence() throws Exception {
        final Var<?> v1 = anyVar("X");
        assertEquals(v1, v1);
    }


    @Test
    public void equality() throws Exception {
        final Var<?> v1 = anyVar("X");
        final Var<?> v2 = anyVar("X");
        assertNotSame(v1, v2);
        assertEquals(v1, v2);
        assertEquals(v2, v1);
    }


    @Test
    public void lowerCaseIsValid() throws Exception {
        final Var<?> v1 = anyVar("lowercase");
        assertSame("lowercase", v1.getName());
        assertEquals(Term.NO_INDEX, v1.getIndex());
    }


    @Test(expected = InvalidTermException.class)
    public void cannotCloneAnonymous() throws Exception {
        Var.copy(Var.ANONYMOUS_VAR);
    }

    @Test
    public void isAnonymousTrue() throws Exception {
        assertTrue(Var.ANONYMOUS_VAR.isAnonymous());
    }

    @Test
    public void isAnonymousFalse() throws Exception {
        assertFalse(anyVar("X").isAnonymous());
    }




}
