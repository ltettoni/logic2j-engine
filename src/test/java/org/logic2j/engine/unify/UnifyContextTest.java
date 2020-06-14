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
import org.logic2j.engine.model.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.logic2j.engine.model.TermApiLocator.termApi;
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
    X.setIndex(1);
    Y = anyVar("Y");
    Y.setIndex(2);
    Z = anyVar("Z");
    Z.setIndex(3);

    Var<?> X2 = anyVar("X2");
    Var<?> X3 = anyVar("X3");
    Var<?> X4 = anyVar("X4");
    Var<?> X5 = anyVar("X5");

    f_ab = termApi().normalize(Struct.valueOf("f", "a", "b"));
    f_aZ = termApi().normalize(Struct.valueOf("f", "a", Z));
    f_XY = termApi().normalize(Struct.valueOf("f", X, Y));
    f_XX = termApi().normalize(Struct.valueOf("f", X2, X2));
    f_aZZ = termApi().normalize(Struct.valueOf("f", "a", X3, X3));
    f_XXa = termApi().normalize(Struct.valueOf("f", X4, X4, "a"));
    f_XXb = termApi().normalize(Struct.valueOf("f", X5, X5, "b"));

    initialContext = new UnifyContext(null, null);
  }


  private UnifyContext bind(Var<?> v, Object t2) {
    logger.info("Binding   : {} -> {}", v, t2);
    UnifyContext m = initialContext;
    assertThat(m).isNotNull();
    UnifyContext m2 = m.bind(v, t2);
    assertThat(m2).isNotNull();
    assertThat(m2).isNotEqualTo(m);
    //
    assertThat(m.reify(v)).isEqualTo(v);
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
    assertThat(m2.reify(X)).isEqualTo(Y);
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
    assertThat(unify(a, a2)).isNotNull();
  }


  @Test
  public void atomToDifferentAtom() {
    assertThat(unify(a, b)).isNull();
  }

  @Test
  public void varToStruct() {
    assertThat(unify(f_ab, X)).isNotNull();
  }


  @Test
  public void structToStruct() {
    assertThat(unify(f_ab, f_XY)).isNotNull();
  }


  @Test
  public void structToStruct2() {
    assertThat(unify(f_aZ, f_XX)).isNotNull();
  }

  @Test
  public void structToStruct3() {
    assertThat(unify(f_aZZ, f_XXa)).isNotNull();
  }

  @Test
  public void structToStruct4() {
    assertThat(unify(f_aZZ, f_XXb)).isNull();
  }


}