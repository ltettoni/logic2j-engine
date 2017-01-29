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

package org.logic2j.engine.unify;

import org.junit.Before;
import org.junit.Test;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.TermApi;
import org.logic2j.engine.model.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.logic2j.engine.model.Var.anyVar;

/**
 * Created by tettoni on 2017-01-20.
 */
public class UnifyContextTest {
  private static final Logger logger = LoggerFactory.getLogger(UnifyContextTest.class);

  protected Var<?> X;

  protected Var<?> Y;

  protected Var<?> Z;

  protected Object _anon;

  protected Object a;

  protected Object b;

  protected Object a2;

  protected Object f_ab, f_aZ, f_XY, f_XX, f_aZZ, f_XXa, f_XXb;

  protected UnifyContext initialContext;

  @Before
  public void configureProlog() {
    a = Struct.valueOf("a");
    b = Struct.valueOf("b");
    a2 = Struct.valueOf("a");
    _anon = Struct.valueOf("_");

    X = anyVar("X");
    X.index = 1;
    Y = anyVar("Y");
    Y.index = 2;
    Z = anyVar("Z");
    Z.index = 3;

    Var<?> X2 = anyVar("X2");
    Var<?> X3 = anyVar("X3");
    Var<?> X4 = anyVar("X4");
    Var<?> X5 = anyVar("X5");

    f_ab = TermApi.normalize(Struct.valueOf("f", "a", "b"));
    f_aZ = TermApi.normalize(Struct.valueOf("f", "a", Z));
    f_XY = TermApi.normalize(Struct.valueOf("f", X, Y));
    f_XX = TermApi.normalize(Struct.valueOf("f", X2, X2));
    f_aZZ = TermApi.normalize(Struct.valueOf("f", "a", X3, X3));
    f_XXa = TermApi.normalize(Struct.valueOf("f", X4, X4, "a"));
    f_XXb = TermApi.normalize(Struct.valueOf("f", X5, X5, "b"));

    initialContext = new UnifyStateByLookup().emptyContext();

  }


  private UnifyContext bind(Var<?> v, Object t2) {
    logger.info("Binding   : {} -> {}", v, t2);
    UnifyContext m = initialContext;
    assertNotNull(m);
    UnifyContext m2 = m.bind(v, t2);
    assertNotNull(m2);
    assertNotSame(m, m2);
    //
    assertSame(v, m.reify(v));
    //logger.info("Reify under original monad: {}", reified(m, v));
    //    logger.info("Term reified with returned UnifyContext: {}", reified(m2, v));
    return m2;
  }


  private UnifyContext unify(Object t1, Object t2) {
    logger.info("Unifying   : {}  ~  {}", t1, t2);
    UnifyContext m = initialContext;
    UnifyContext m2 = m.unify(t1, t2);
    if (m2 != null) {
      logger.info("Unified");
      logger.info("Monad after: {}", m2);
      //      logger.info("Terms after: {}  =  {}", reified(m2, t1), reified(m2, t2));
    } else {
      logger.info("Not unified");
    }
    return m2;
  }



  @Test
  public void bindVarToLiteral() {
    bind(X, "literal");
  }

  @Test
  public void bindVarToVar() {
    UnifyContext m2 = bind(X, Y);
    assertSame(Y, m2.reify(X));
  }

  @Test
  public void varToAtom() {
    unify(X, a);
  }

  @Test
  public void varToAnon() {
    unify(X, _anon);
  }

  @Test
  public void atomToVar() {
    unify(a, X);
  }


  @Test
  public void atomToSameAtom() {
    assertNotNull(unify(a, a2));
  }


  @Test
  public void atomToDifferentAtom() {
    assertNull(unify(a, b));
  }

  @Test
  public void varToStruct() {
    assertNotNull(unify(f_ab, X));
  }


  @Test
  public void structToStruct() {
    assertNotNull(unify(f_ab, f_XY));
  }


  @Test
  public void structToStruct2() {
    assertNotNull(unify(f_aZ, f_XX));
  }

  @Test
  public void structToStruct3() {
    assertNotNull(unify(f_aZZ, f_XXa));
  }

  @Test
  public void structToStruct4() {
    assertNull(unify(f_aZZ, f_XXb));
  }


}