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

package org.logic2j.engine.predicates.impl;

import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.predicates.external.RDBCompatiblePredicate;
import org.logic2j.engine.predicates.impl.math.Pred2;
import org.logic2j.engine.unify.UnifyContext;

import java.util.function.Function;

/**
 * Unification operator "=", used for testing or setting values.
 */
public class Eq<T> extends Pred2<T, T> implements RDBCompatiblePredicate {
  public Eq(Binding<T> t1, Binding<T> t2) {
    super("=", t1, t2);
    setImage(Function.identity());
    setPreimage(Function.identity());
  }

  public Eq(Binding<Term> t1, Term t2) {
    super("=", t1, t2);
    setImage(Function.identity());
    setPreimage(Function.identity());
  }

  @Override
  protected Integer unification(UnifyContext currentVars, Object n0, Object n1) {
    if (isFreeVar(n0) && isFreeVar(n1)) {
      // Special cas because Pred2 cannot handle two free vars. Eq can.
      return unifyAndNotify(currentVars, n0, n1);
    }
    return super.unification(currentVars, n0, n1);
  }

}
