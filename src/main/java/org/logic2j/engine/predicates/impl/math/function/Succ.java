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

package org.logic2j.engine.predicates.impl.math.function;

import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.math.Pred2;

import static org.logic2j.engine.model.SimpleBindings.bind;

/**
 * Successor value.
 */
public class Succ<T extends Number> extends Pred2<T, T> {
  private static final String PREDICATE_NAME = "succ";


  public Succ(Binding<T> n0, Binding<T> n1) {
    super(PREDICATE_NAME, n0, n1);
    setImage(t -> (T) nextNumber.apply(t));
    setPreimage(t -> (T) previousNumber.apply(t));
  }

  // --------------------------------------------------------------------------
  // Convenience (syntactic sugar) constructors
  // --------------------------------------------------------------------------

  public Succ(T n0, T n1) {
    this(bind(n0), bind(n1));
  }

  public Succ(T n0, Var<T> v1) {
    this(bind(n0), v1);
  }

  public Succ(Var<T> v0, T n1) {
    this(v0, bind(n1));
  }

  // --------------------------------------------------------------------------
  // Implementation
  // --------------------------------------------------------------------------

  private static final NumericFunction nextNumber = new NumericFunction() {
    @Override
    public Integer onInteger(Integer arg) {
      return arg + 1;
    }

    @Override
    public Long onLong(Long arg) {
      return arg + 1L;
    }

    @Override
    public Float onFloat(Float arg) {
      return arg + 1.0f;
    }

    @Override
    public Double onDouble(Double arg) {
      return arg + 1.0;
    }
  };

  private static final NumericFunction previousNumber = new NumericFunction() {
    @Override
    public Integer onInteger(Integer arg) {
      return arg - 1;
    }

    @Override
    public Long onLong(Long arg) {
      return arg - 1L;
    }

    @Override
    public Float onFloat(Float arg) {
      return arg - 1.0f;
    }

    @Override
    public Double onDouble(Double arg) {
      return arg - 1.0;
    }
  };

}
