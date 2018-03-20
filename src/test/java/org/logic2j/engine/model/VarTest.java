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
import org.logic2j.engine.exception.InvalidTermException;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.logic2j.engine.model.Var.anyVar;
import static org.logic2j.engine.predicates.Predicates.anon;

public class VarTest {

  @Test
  public void constructorValid() {
    final Var<?> v1 = anyVar("X");
    assertSame("X", v1.getName());
    assertEquals(Term.NO_INDEX, v1.getIndex());
  }

  @Test(expected = InvalidTermException.class)
  public void constructorNull() {
    anyVar(null);
  }

  @Test(expected = InvalidTermException.class)
  public void constructorSameAsAnonymous1() {
    anyVar("_");
  }

  @Test(expected = InvalidTermException.class)
  public void constructorSameAsAnonymous() {
    anyVar(new StringBuilder().append('_').toString());
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
  public void automaticName() {
    final Var<String> stringVar = new Var<>(String.class);
    final String name1 = stringVar.getName();
    assertThat(name1, not(isEmptyString()));
    assertThat(name1, startsWith("_"));
    final Var<String> stringVar2 = new Var<>(String.class);
    final String name2 = stringVar2.getName();
    assertThat(name2, not(is(name1)));
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
    Var.copy(anon());
  }

  @Test
  public void isAnonymousTrue() {
    assertTrue(anon().isAnon());
  }

  @Test
  public void isAnonymousFalse() {
    assertFalse(anyVar("X").isAnon());
  }



}
