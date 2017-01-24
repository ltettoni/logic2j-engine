/*
 * logic2j - "Bring Logic to your Java" - Copyright (C) 2011 Laurent.Tettoni@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.logic2j.predsolver.model;

import org.logic2j.predsolver.exception.InvalidTermException;
import org.logic2j.predsolver.visitor.TermVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;

/**
 * This class represents a variable term. Variables are identified by a name (which must starts with an upper case letter) or the anonymous
 * ('_') name.
 * Note: This class MUST be immutable.
 * Switch logging for this class to DEBUG level in order to have the details of variables, such as the variable index.
 */
public class Var<T> extends Term implements Comparable<Var<T>> {
  private static final Logger logger = LoggerFactory.getLogger(Var.class);

  private static final long serialVersionUID = 1L;

  public static final String WHOLE_SOLUTION_VAR_NAME = ".".intern();

  /**
   * Name of the anonymous variable is always "_". This constant is internalized, you
   * can safely compare it with ==.
   */
  public static final String ANONYMOUS_VAR_NAME = "_".intern();

  /**
   * Singleton anonymous variable. You can safely compare them with ==.
   */
  public static final Var<Void> ANONYMOUS_VAR = new Var<Void>();

  /**
   * Singleton "special" var that holds the value of a whole goal.
   */
  public static final Var<Object> WHOLE_SOLUTION_VAR = new Var<Object>(Object.class, WHOLE_SOLUTION_VAR_NAME);

  public static final Comparator<Var<?>> COMPARATOR_BY_NAME = (left, right) -> left.getName().compareTo(right.getName());

  /**
   * Hold the type at runtime - due to erasures.
   */
  private final Class<T> type;

  /**
   * The immutable name of the variable, usually starting with uppercase when this Var was instantiated by the default parser, but when instantiated
   * by {@link #Var(Class, CharSequence)} it can actually be anything (although it may not be the smartest idea).<br/>
   * A value of Var.ANONYMOUS_VAR_NAME means it's the anonymous variable<br/>
   * Note: all variables' names are internalized, i.e. it is legal to compare their names with ==.
   */
  private final String name;

  /**
   * Create the anonymous variable singleton.
   */
  private Var() {
    this.name = ANONYMOUS_VAR_NAME;
    this.type = /* FIXME */ null;
    this.index = NO_INDEX;  // Actually the default value but let's enforce that here
  }

  /**
   * Creates a variable identified by a name.
   * <p/>
   * The name must starts with an upper case letter or the underscore. If an underscore is specified as a name, the variable is anonymous.
   *
   * @param theName is the name
   * @throws InvalidTermException if n is not a valid Prolog variable name
   * @note Internally the {@link #name} is {@link String#intern()}alized so it's OK to compare by reference.
   */
  public Var(Class<T> theType, CharSequence theName) {
    if (theName == Var.ANONYMOUS_VAR_NAME) {
      throw new InvalidTermException("Must not instantiate the anonymous variable (which is a singleton)!");
    }
    if (theName == null) {
      throw new InvalidTermException("Name of a variable cannot be null");
    }
    final String str = theName.toString();
    if (str.isEmpty()) {
      throw new InvalidTermException("Name of a variable may not be the empty String");
    }
    this.name = str.intern();
    this.type = theType;
  }

//  public Var(CharSequence theName) {
//    this(/* FIXME */ null, theName);
//  }

  // ---------------------------------------------------------------------------
  // Static factories
  // ---------------------------------------------------------------------------

  public static Var<Object> anyVar(CharSequence varName) {
    return new Var<>(Object.class, varName);
  }

  public static Var<String> strVar(CharSequence varName) {
    return new Var<>(String.class, varName);
  }

  public static Var<Integer> intVar(CharSequence varName) {
    return new Var<>(Integer.class, varName);
  }

  public static Var<Long> longVar(CharSequence varName) {
    return new Var<>(Long.class, varName);
  }

  /**
   * Copy constructor
   * Clones the name and the index.
   *
   * @param original
   * @throws InvalidTermException If you try to clone the anonymous variable!
   */
  public static <Q> Var<Q> copy(Var<Q> original) {
    if (original.name == Var.ANONYMOUS_VAR_NAME) {
      throw new InvalidTermException("Cannot clone the anonymous variable via a copy constructor!");
    }
    final Var<Q> cloned = new Var<Q>(original.type, original.name);
    cloned.index = original.getIndex();
    return cloned;
  }



  // ---------------------------------------------------------------------------
  // Accessors
  // ---------------------------------------------------------------------------

  /**
   * Gets the name of the variable.
   *
   * @note Names are {@link String#intern()}alized so OK to check by reference (with ==)
   */
  public String getName() {
    return this.name;
  }

  public Class<?> getType() {
    return type;
  }

  /**
   * Tests if this variable is anonymous.
   */
  public boolean isAnonymous() {
    return this == ANONYMOUS_VAR || this.name == ANONYMOUS_VAR_NAME; // Names are {@link String#intern()}alized so OK to check by reference
  }


  // ---------------------------------------------------------------------------
    // TermVisitor
    // ---------------------------------------------------------------------------

    @Override
    public <T> T accept(TermVisitor<T> theVisitor) {
        return theVisitor.visit(this);
    }

    // ---------------------------------------------------------------------------
  // Template methods defined in abstract class Term
  // ---------------------------------------------------------------------------

  /**
   * Just add this to theCollectedTerms and set {@link Term#index} to {@link Term#NO_INDEX}.
   *
   * @param theCollectedTerms
   */
  void collectTermsInto(Collection<Object> theCollectedTerms) {
    this.index = NO_INDEX;
    theCollectedTerms.add(this);
  }


  Object factorize(Collection<Object> theCollectedTerms) {
    // If this term already has an equivalent in the provided collection, return that one
    final Object alreadyThere = findStructurallyEqualWithin(theCollectedTerms);
    if (alreadyThere != null) {
      return alreadyThere;
    }
    // Not found by structural equality, we match variables by their name
    // TODO I'm not actually sure why we do this - we should probably log and identify why this case
    for (final Object term : theCollectedTerms) {
      if (term instanceof Var) {
        final Var<?> var = (Var) term;
        if (this.getName().equals(var.getName())) {
          return var;
        }
      }
    }
    return this;
  }

  /**
   * @param theOther
   * @return true only when references are the same, otherwise two distinct {@link Var}s will always be considered different, despite
   * their name, index, or whatever.
   */
  boolean structurallyEquals(Object theOther) {
    return theOther == this; // Check memory reference only
  }

  /**
   * Assign a new {@link Term#index} to a Var if it was not assigned before.
   */
  int assignIndexes(int theIndexOfNextNonIndexedVar) {
    if (this.index != NO_INDEX) {
      // assert false : "We are re-indexing an indexed Var but return a wrong value";
      // Already assigned, avoid changing the index! Do nothing
      return theIndexOfNextNonIndexedVar; // return the argument, since we did not assign anything new
    }
    if (isAnonymous()) {
      // Anonymous variable is not a var, don't count it, but assign an
      // index that is different from NO_INDEX but that won't be ever used
      this.index = ANON_INDEX;
      return theIndexOfNextNonIndexedVar; // return same index since we did nothing
    }
    // Index this var
    this.index = (short) theIndexOfNextNonIndexedVar;
    return theIndexOfNextNonIndexedVar + 1;
  }

  // ---------------------------------------------------------------------------
  // Methods of java.lang.Object
  // ---------------------------------------------------------------------------

  @Override
  public int hashCode() {
    return this.name.hashCode() ^ this.index;
  }

  /**
   * Equality is done by name and index - but does that make any sense?
   */
  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Var)) {
      return false;
    }
    final Var<?> that = (Var) other;
    return this.name == that.name && this.index == that.index; // Names are {@link String#intern()}alized so OK to check by reference
  }

  @Override
  public String toString() {
    if (logger.isDebugEnabled()) {
      return this.name + '#' + this.getIndex();
    }
    return this.name;
  }

  /**
   * Just to allow odering of Var, by their name
   *
   * @param that
   * @return Comparison based on #getName()
   */
  @Override
  public int compareTo(Var<T> that) {
    return this.getName().compareTo(that.getName());
  }
}
