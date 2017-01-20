package org.logic2j.predsolver.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class StructTest {


    // ---------------------------------------------------------------------------
    // Struct
    // ---------------------------------------------------------------------------

    @Test
    public void struct0() throws Exception {
        Struct a1 = new Struct("f");
        assertEquals(0, a1.getArity());
        assertSame("f", a1.getName());
        Struct a2 = new Struct("f");
        assertNotSame(a1, a2);
        assertEquals(a1, a2);
    }

    @Test
    public void atomAsString() throws Exception {
        Object a1 = Struct.atom("f");
        assertTrue(a1 instanceof String);
        assertSame("f", a1);
        Object a2 = Struct.atom("f");
        assertSame(a1, a2);
    }


    @Test
    public void atomAsStruct() throws Exception {
        Object a1 = Struct.atom("true");
        assertTrue(a1 instanceof Struct);
        assertSame("true", ((Struct) a1).getName());
        Object a2 = Struct.atom("true");
        assertNotSame(a1, a2);
    }

    @Test
    public void struct2() throws Exception {
        Struct a1 = new Struct("f", "a", "b");
        assertEquals(2, a1.getArity());
        assertSame("f", a1.getName());
        assertSame("a", a1.getArg(0));
        assertSame("b", a1.getArg(1));
        Struct a2 = new Struct("f", "a", "b");
        assertNotSame(a1, a2);
        assertEquals(a1, a2);
        assertNotEquals(a1, new Struct("f", "b", "a"));
    }


}