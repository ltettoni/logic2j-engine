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
package org.logic2j.predsolver.model;

import org.junit.Test;
import org.logic2j.predsolver.exception.InvalidTermException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.logic2j.predsolver.model.Var.anyVar;
import static org.logic2j.predsolver.predicates.Predicates.anon;

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
    assertFalse(anyVar("X").structurallyEquals(anyVar("Y")));
    final Var<?> x1 = anyVar("X");
    final Var<?> x2 = anyVar("X");
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
  public void collectTerms() {
    Term term;
    //
    term = Struct.valueOf("p", "X", 2);
    logger.info("Flat terms: {}", TermApi.collectTerms(term));
    //
    term = Struct.valueOf("a", new Struct("b"), "c");
    logger.info("Flat terms: {}", TermApi.collectTerms(term));
    //
    term = new Struct(Struct.FUNCTOR_CLAUSE, new Struct("a", Struct.valueOf("p", "X", "Y")), Struct.valueOf("p", "X", "Y"));
    logger.info("Flat terms: {}", TermApi.collectTerms(term));
    //
    final Term clause = new Struct(Struct.FUNCTOR_CLAUSE, new Struct("a", Struct.valueOf("p", "X", "Y")), Struct.valueOf("p", "X", "Y"));
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
    nbVars = TermApi.assignIndexes(anyVar("X"), 0);
    assertEquals(1, nbVars);
    nbVars = TermApi.assignIndexes(anon(), 0);
    assertEquals(0, nbVars);
    //
    nbVars = TermApi.assignIndexes(Long.valueOf(2), 0);
    assertEquals(0, nbVars);
    nbVars = TermApi.assignIndexes(Double.valueOf(1.1), 0);
    assertEquals(0, nbVars);
  }



  @Test(expected = InvalidTermException.class)
  public void functorFromSignatureFails() {
    TermApi.functorFromSignature("toto4");
  }


  @Test
  public void functorFromSignature1() {
    assertEquals("toto", TermApi.functorFromSignature("toto/4"));
  }



  @Test(expected = InvalidTermException.class)
  public void arityFromSignatureFails() {
    TermApi.arityFromSignature("toto4");
  }

  @Test
  public void arityFromSignature1() {
    assertEquals(4, TermApi.arityFromSignature("toto/4"));
  }

  @Test
  public void quoteIfNeeded() {
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
