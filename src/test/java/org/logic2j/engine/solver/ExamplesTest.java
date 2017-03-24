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

package org.logic2j.engine.solver;

import org.junit.Ignore;
import org.junit.Test;
import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.Digit;
import org.logic2j.engine.predicates.Even;
import org.logic2j.engine.predicates.impl.firstorder.Count;
import org.logic2j.engine.predicates.impl.firstorder.Exists;
import org.logic2j.engine.predicates.internal.And;
import org.logic2j.engine.predicates.internal.Call;
import org.logic2j.engine.predicates.internal.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.model.SimpleBindings.bind;
import static org.logic2j.engine.model.Var.intVar;
import static org.logic2j.engine.model.Var.longVar;
import static org.logic2j.engine.predicates.Predicates.*;

/**
 * Examples to explain what logic2j-engine is.
 */
public class ExamplesTest {
  private static final Logger logger = LoggerFactory.getLogger(ExamplesTest.class);
  private SolverApi solver = new SolverApi();

  @Test
  @Ignore("Do not run not workable")
  public void sampleData() {
    final Var<Integer> x = intVar("X");
    final Var<Integer> y = intVar("Y");
    final Term expr = new Exists(new And(new Digit(x), new Square(x, y), new Is(x, new Mod(y, 10))));
    final long nbr = solver.solve(expr).count();
  }

  private class Is extends Struct{
    public Is(Object... argList) {
      super("is", argList);
    }
  }

  private class Square extends Struct {
    public Square(Object... argList) {
      super("square", argList);
    }
  }

  private class Mod  extends Struct{
    public Mod(Object... argList) {
      super("mod", argList);
    }
  }
}