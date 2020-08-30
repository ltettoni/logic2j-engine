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

import java.util.Arrays;
import java.util.Locale;
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.Constant;
import org.logic2j.engine.model.SimpleBindings;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.predicates.external.RDBFunctionPredicate;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.predicates.impl.math.Pred2;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.unify.UnifyContext;

/**
 * Convert to lowercase (in the English locale)
 */
public class Format extends FOPredicate {

  private static final String PREDICATE_NAME = "format";

  public Format(Object... args) {
    super(PREDICATE_NAME, args);
  }

  public static Format valueOf(Struct struct) {
    if (struct.getName() == PREDICATE_NAME && struct.getArity()>=2) {
      return new Format(struct.getArgs());
    }
    return null;
  }

  @Override
  public int predicateLogic(UnifyContext currentVars) {
    final Object[] reified = new Object[getArity()];
    for (int i=0; i<getArity(); i++) {
      Object v = currentVars.reify(getArg(i));
      if (v instanceof Constant<?>) {
        v = ((Constant<?>)v).toScalar();
      }
      reified[i] = v;
    }

    for (int i=1; i<getArity(); i++) {
      ensureBindingIsNotAFreeVar(reified[i], i);
    }

    final String formatted = String.format(reified[1].toString(), Arrays.copyOfRange(reified, 2, reified.length));

    return unifyAndNotify(currentVars, reified[0], formatted);
  }
}
