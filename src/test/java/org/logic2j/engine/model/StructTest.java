/*
 * logic2j - "Bring Logic to your Java" - Copyright (c) 2017 Laurent.Tettoni@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.logic2j.engine.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class StructTest {


  @Test
  public void struct0() {
    Struct a1 = new Struct("f");
    assertEquals(0, a1.getArity());
    assertSame("f", a1.getName());
    Struct a2 = new Struct("f");
    assertNotSame(a1, a2);
    assertEquals(a1, a2);
  }

  @Test
  public void atomAsString() {
    Object a1 = Struct.atom("f");
    assertTrue(a1 instanceof String);
    assertSame("f", a1);
    Object a2 = Struct.atom("f");
    assertSame(a1, a2);
  }


  @Test
  public void atomAsStruct() {
    Object a1 = Struct.atom("true");
    assertTrue(a1 instanceof Struct);
    assertSame("true", ((Struct) a1).getName());
    Object a2 = Struct.atom("true");
    assertNotSame(a1, a2);
  }

  @Test
  public void struct2() {
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