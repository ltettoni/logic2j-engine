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

import java.util.Locale;
import org.logic2j.engine.model.Binding;
import org.logic2j.engine.model.SimpleBindings;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.predicates.external.RDBFunctionPredicate;
import org.logic2j.engine.predicates.impl.math.Pred2;

/**
 * Convert to lowercase (in the English locale)
 */
public class LowerCase<T extends CharSequence, R extends CharSequence> extends Pred2<T, R> implements RDBFunctionPredicate {

  private static final String PREDICATE_NAME = "lc";


  public LowerCase(Binding<T> arg0, Binding<R> arg1) {
    super(PREDICATE_NAME, arg0, arg1);
    setPreimage(null); // Preimage function not defined
    setImage(str -> (R) str.toString().toLowerCase(Locale.ENGLISH));
  }


  public static LowerCase valueOf(Struct struct) {
      //noinspection StringEquality
      if (struct.getName() == PREDICATE_NAME && struct.getArity()==2) { // Names are {@link String#intern()}alized so OK to check by reference
      return new LowerCase(SimpleBindings.newBinding(struct.getArg(0)),
              SimpleBindings.newBinding(struct.getArg(1)));
    }
    return null;
  }

  @Override
  public String[] sqlFunctionTextFormat() {
    return new String[] {"lower({0})={1}", null, "lower({0})"};
  }
}
