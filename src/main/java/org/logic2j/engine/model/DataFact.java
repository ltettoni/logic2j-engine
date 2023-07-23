/*
 * logic2j - "Bring Logic to your Java" - Copyright (c) 2018 Laurent.Tettoni@gmail.com
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


import org.logic2j.engine.exception.InvalidTermException;

import java.util.Arrays;

/**
 * Represent one constant data element that can unify to an n-arity flat {@link Struct},
 * for example functor(a, 'B', 12).
 * This is intended for efficient storage of data instead of using {@link Struct}.
 * This is an immutable value object.
 */
public final class DataFact {

  private final Object[] elements;

  /**
   * functor and arguments
   *
   * @param arguments The element at index 0 is assumed to be the functor. At least 2 elements required.
   */
  public DataFact(Object... arguments) {
    // Thorough checking args and reporting of exceptions
    if (arguments == null || arguments.length < 2) {
      throw new InvalidTermException("Dubious instantiation of DataFact with null record, or arity < 1");
    }
    for (int i = 1; i < arguments.length; i++) {
      if (arguments[i]==null) {
        throw new InvalidTermException("Cannot instantiate DataFact when value at index " + i + " is null. Complete array was: " + Arrays.asList(arguments));
      }
    }
    if (! (arguments[0] instanceof CharSequence)) {
      throw new InvalidTermException("First argument of a DataFact is a functor and must be a character sequence, was " + arguments[0] + ". Complete array was: " + Arrays.asList(arguments));
    }

    this.elements = new Object[arguments.length];

    // Functor
    this.elements[0] = (String.valueOf(arguments[0])).intern();

    for (int i = 1; i < arguments.length; i++) {
      // We used to use the TermApi, this is probably not a good idea (static reference)
      // this.elements[i] = termApi().valueOf(arguments[i]);

      // Strings are internalized for unification
      if (arguments[i] instanceof CharSequence) {
        this.elements[i] = (String.valueOf(arguments[i])).intern();
      } else {
        this.elements[i] = arguments[i];
      }

    }
  }

  // ---------------------------------------------------------------------------
  // Accessors
  // ---------------------------------------------------------------------------

  public String functor() {
    return (String) this.elements[0];
  }

  public int arity() {
    return this.elements.length - 1;
  }

  public Object[] getElements() {
    return this.elements;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + Arrays.asList(this.elements);
  }

}
