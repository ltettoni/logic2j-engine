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

import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.TermApi;
import org.logic2j.engine.model.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * A monad-like object that allows dereferencing variables to their effective current values,
 * or to set values to free variables (and return a new UnifyContext).
 * This is a lightweight object that is very frequently instantiated.
 * It contains logic to bind variables, unify structures, and reify variables to their effective current
 * values. The real data store is the more heavy {@link UnifyStateByLookup} object.
 */
public class UnifyContext {
  private static final Logger logger = LoggerFactory.getLogger(UnifyContext.class);
  //    static final Logger audit = LoggerFactory.getLogger("audit");

  /**
   * Holds the state of all variables. Immutable and shared by all various {@link UnifyContext}s
   * returned during the solving of a goal.
   */
  private final UnifyStateByLookup stateStorage;

  final int currentTransaction;

  /**
   * The highest variable index seen so far. When recurse goals through inference, we need a new set
   * of free variables for evey goal (think of fact(X) :- fact(X1), ...). This is achieved by
   * adding an offset to all variable of a goal, hence emulating new free vars.
   */
  private int topVarIndex;

  /**
   * Initial facade to all empty vars.
   *
   * @param stateStorage
   */
  UnifyContext(UnifyStateByLookup stateStorage) {
    this.stateStorage = stateStorage;
    this.currentTransaction = 0;
    this.topVarIndex = 0;
    //        audit.info("New at t={}", currentTransaction);
    //        audit.info("    this={}", this);
  }

  /**
   * A new facade to the state of vars after one has been set.
   *
   * @param previous
   */
  UnifyContext(UnifyContext previous) {
    this.stateStorage = previous.stateStorage;
    this.topVarIndex = previous.topVarIndex;
    this.currentTransaction = previous.currentTransaction + 1;
  }

  /**
   * Increment and/or obtain top variable index.
   *
   * @param incrementOrZero
   * @return The new top variable index, after adding incrementOrZero to its previous value
   */
  public int topVarIndex(int incrementOrZero) {
    this.topVarIndex += incrementOrZero;
    return this.topVarIndex;
  }


  /**
   * Instantiate a new Var and assign a unique index
   *
   * @param theName
   * @return A new Var uniquely indexed
   */
  public Var<?> createVar(String theName) {
    final Var<?> var = new Var<Object>(Object.class, theName);
    var.index = topVarIndex++;
    return var;
  }

  /**
   * Bind var to ref (var will be altered in the returned UnifyContext); ref is untouched.
   * <p>
   * (private except that used from test case)
   *
   * @param var
   * @param ref
   * @return
   */
  UnifyContext bind(Var<?> var, Object ref) {
    if (var == ref) {
      if (logger.isDebugEnabled()) {
        logger.debug("Not mapping {} onto itself", var);
      }
      return this;
    }
    //        audit.info("Bind   {} -> {} at t=" + this.currentTransaction, var, ref);
    return stateStorage.bind(this, var, ref);
  }


  /**
   * In principle one must use the recursive form reify()
   *
   * @param theVar
   * @return The dereferenced content of theVar, or theVar if it was free
   */
  private Object finalValue(Var<?> theVar) {
    final Object dereference = this.stateStorage.dereference(theVar, this.currentTransaction);
    return dereference;
  }

  /**
   * Resolve variables to their values.
   *
   * @param term
   * @return The dereferenced content of term, or theVar if it was free, or null if term is null
   */
  public Object reify(Object term) {
    if (TermApi.isFreeVar(term)) {
      term = finalValue((Var) term);
      // The var might end up on a Struct, that needs recursive reification
    }
    if (term instanceof Struct) {
      //            audit.info("Reify Struct at t={}  {}", this.currentTransaction, term);
      final Struct s = (Struct) term;
      if (s.getIndex() == 0) {
        // Structure is an atom or a constant term - no need to further transform
        return term;
      }
      // Structure has arguments
      final Object[] reifiedArgs = Arrays.stream(s.getArgs()).map(this::reify).toArray(Object[]::new);
      final Struct res = new Struct(s, reifiedArgs);
      if (s.getIndex() > 0) {
        // The original structure had variables, maybe the cloned one will still have (if those were free)
        // We need to reassign indexes. It's costly, unfortunately.
        TermApi.assignIndexes(res, 0);
      }
      //            audit.info("               yields {}", res);
      return res;
    }
    return term;
  }

  /**
   * Unify two terms. Most optimal invocation is Var against non-Var.
   *
   * @param term1
   * @param term2
   * @return
   */
  public UnifyContext unify(Object term1, Object term2) {
    //        audit.info("Unify  {}  ~  {}", term1, term2);
    if (term1 == term2) {
      return this;
    }
    if (TermApi.isFreeVar(term2)) {
      // Switch arguments - we prefer having term1 being the var.
      // Notice that formally, we should check  && !(term1 instanceof Var)
      // to avoid possible useless switching when unifying Var <-> Var.
      // However, the extra instanceof total costs 3% more than a useless switch.
      final Object term1held = term1;
      term1 = term2;
      term2 = term1held;
    }
    if (TermApi.isFreeVar(term1)) {
      // term1 is a Var: we need to check if it is bound or not
      Var<?> var1 = (Var) term1;
      final Object final1 = finalValue(var1);
      if (!(TermApi.isFreeVar(final1))) {
        // term1 is bound - unify
        return unify(final1, term2);
      }
      // Ended up with final1 being a free Var, so term1 was a free var
      var1 = (Var) final1;
      // free Var var1 need to be bound
      if (TermApi.isFreeVar(term2)) {
        // Binding two vars
        final Var<?> var2 = (Var) term2;
        // Link one to two (should we link to the final or the initial value???)
        // Now do the binding of two vars
        return bind(var1, var2);
      } else {
        // Do the binding of one var to a literal
        return bind(var1, term2);
      }
    } else if (term1 instanceof Struct) {
      // Case of Struct <-> Var: already taken care of by switching, see above
      if (!(term2 instanceof Struct)) {
        // Not unified - we can only unify 2 Struct
        return null;
      }
      final Struct s1 = (Struct) term1;
      final Struct s2 = (Struct) term2;
      // The two Struct must have compatible signatures (functor and arity)
      //noinspection StringEquality
      if (s1.getPredicateSignature() != s2.getPredicateSignature()) {
        return null;
      }
      // Now we will unify all arguments, stopping at the first that do not match
      final Object[] s1Args = s1.getArgs();
      final Object[] s2Args = s2.getArgs();
      final int arity = s1Args.length;
      UnifyContext runningMonad = this;
      for (int i = 0; i < arity; i++) {
        runningMonad = runningMonad.unify(s1Args[i], s2Args[i]);
        if (runningMonad == null) {
          // Struct sub-element not unified - fail the whole unification
          return null;
        }
      }
      // All matched, return the latest monad
      return runningMonad;
    } else {
      return term1.equals(term2) ? this : null;
    }
  }

  @Override
  public String toString() {
    return "vars#" + this.currentTransaction + stateStorage.toString();
  }

}
