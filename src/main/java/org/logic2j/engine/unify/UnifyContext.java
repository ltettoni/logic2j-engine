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

import org.logic2j.engine.model.DataFact;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.solver.Solver;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.logic2j.engine.model.TermApiLocator.termApi;

/**
 * A monad-like object that allows dereferencing variables to their effective current values,
 * or to set values of free variables (and return a new UnifyContext).
 * This is a lightweight object that is very frequently instantiated.
 * It contains logic to bind variables, unify structures, and reify variables to their effective current
 * values. The real data store for variable's values is the more complex {@link UnifyStateByLookup} object.
 * <p/>
 * In the early phases of logic2j we needed to unify (bind) variables, then undo the work explicitly while backtracking.
 * This required cumbersome programming. Now UnifyContext is like a state monad or value object: it is immutable, and
 * a new value is returned after a "bind" that results in effectively setting a value to a variable.
 * Backtracking is achieved by just forgetting the context, and continuing with a previous context.
 * <p/>
 * This context also stores the current {@link SolutionListener} although that object is not needed for inference.
 * In a previous version of logic2j, it was not stored here, the consequence was that most methods in the code, and in particular
 * user-level libraries, received systematically the two arguments (the {@link UnifyContext} and the {@link SolutionListener})).
 * Since logic2j-engine, the SolutionListener is wrapped in the context to shorten methods signatures.
 */
public class UnifyContext {
  //    static final Logger audit = LoggerFactory.getLogger("audit");

  /**
   * Holds the state of all variables. Immutable and shared by all various {@link UnifyContext}s
   * returned during the solving of a goal.
   */
  private final UnifyStateByLookup stateStorage;

  /**
   * Hold the solver algorithm currently in use.
   */
  private final Solver solver;

  /**
   * Just stored in this context object for convenience (to avoid passing two args in all methods throughout the code base).
   * Not functionally used in this class, but hold as an important context reference.
   * Only exposed with {@link #getSolutionListener()}.
   */
  private SolutionListener solutionListener;

  /**
   * TODO Document this
   */
  final int currentTransaction;

  /**
   * The highest variable index seen so far. When solving goals recursively through inference, we need a new set
   * of free variables when we consider solving a new clause (think of fact(X) :- fact(X1), ...). This is achieved by
   * adding an offset to all variable of a goal, hence emulating "new" free vars.
   */
  private int topVarIndex;

  /**
   * Create initial {@link UnifyContext} with all empty vars.
   *
   * @param solver
   * @param solutionListener
   */
  public UnifyContext(Solver solver, SolutionListener solutionListener) {
    this(new UnifyStateByLookup(), solver, solutionListener);
  }


  /**
   * Initial facade to all empty vars.
   *
   * @param stateStorage
   * @param solver
   * @param solutionListener
   */
  private UnifyContext(UnifyStateByLookup stateStorage, Solver solver, SolutionListener solutionListener) {
    this.stateStorage = stateStorage;
    this.solver = solver;
    this.solutionListener = solutionListener;
    this.currentTransaction = 0;
    this.topVarIndex = 0;
    //        audit.info("New at t={}", currentTransaction);
    //        audit.info("    this={}", this);
  }

  /**
   * Copy constructor, will share the same state of variables as the original one.
   *
   * @param original The original to copy
   */
  UnifyContext(UnifyContext original, int newTransaction) {
    this.stateStorage = original.stateStorage;
    this.solver = original.solver;
    this.solutionListener = original.solutionListener;
    this.topVarIndex = original.topVarIndex;
    this.currentTransaction = original.currentTransaction + newTransaction;
  }


  /**
   * Copy and set a new {@link SolutionListener}
   *
   * @param newListener
   * @return A copy with the specified {@link SolutionListener}
   */
  public UnifyContext withListener(SolutionListener newListener) {
    final UnifyContext copy = new UnifyContext(this, 0);
    copy.solutionListener = newListener;
    return copy;
  }

  /**
   * Increment and obtain new top variable index.
   *
   * @param incrementOrZero Amount to increment, specify zero to get the current value
   * @return The new top variable index, after adding incrementOrZero to its previous value
   */
  public int topVarIndex(int incrementOrZero) {
    this.topVarIndex += incrementOrZero;
    return this.topVarIndex;
  }


  // --------------------------------------------------------------------------
  // Unification methods
  // --------------------------------------------------------------------------

  /**
   * Bind var to ref (var will be altered in the returned UnifyContext); ref is untouched.
   * <p/>
   * (private except that used from test case)
   *
   * @note This method may bind a Var to the anonymous variable - this case to avoid is handled in calling code.
   * @param var
   * @param ref
   * @return
   */
  UnifyContext bind(Var<?> var, Object ref) {
    if (var == ref) {
      return this;
    }
    //        audit.info("Bind   {} -> {} at t=" + this.currentTransaction, var, ref);
    return stateStorage.bind(this, var, ref);
  }


  /**
   * Unify two terms. The most optimal invocation is Var against non-Var.
   *
   * @param term1
   * @param term2
   * @return null if not unifyable, this if unifyable without any change to a free variable,
   * and a new UnifyContext if any var(s) were changed.
   */
  public UnifyContext unify(Object term1, Object term2) {
    //        audit.info("Unify  {}  ~  {}", term1, term2);
    if (term1 == term2) {
      return this;
    }
    if (termApi().isFreeVar(term2)) {
      // Switch arguments - we prefer having term1 being the variable.
      // Notice that formally, we should check  && !(term1 instanceof Var)
      // to avoid possible useless switching when unifying Var <-> Var.
      // However, the extra instanceof total costs 3% more than a useless switch.
      final Object term1held = term1;
      term1 = term2;
      term2 = term1held;
    }
    if (termApi().isFreeVar(term1)) {
      // term1 is a Var: we need to check if it is bound or not
      Var<?> var1 = (Var<?>) term1;
      if (var1.isAnon()) {
        // Unification of a free var to anonymous must succeed but won't bind the variable
        // to the anonymous. We return this context to indicate unification success but no change.
        return this;
      }
      // Check if the reified term1 is bound or not
      final Object final1 = reifiedVar(var1);
      if (!(termApi().isFreeVar(final1))) {
        // Yes term1 was a real value we now unify it with term2
        return unify(final1, term2);
      }
      // Ended up with final1 being a free Var, so term1 was a free var
      var1 = (Var<?>) final1;
      // free Var var1 need to be bound
      if (termApi().isFreeVar(term2)) {
        final Var<?> var2 = (Var<?>) term2;
        if (var2.isAnon()) {
          // Unification of a free var to anonymous must succeed but won't bind the variable
          // to the anonymous. We return this context to indicate unification success but no change.
          return this;
        }
        // Binding two vars
        // Link one to two (should we link to the final or the initial value???)
        // Now do the binding of two vars
        return bind(var1, var2);
      } else {
        // Do the binding of one var to a literal
        return bind(var1, term2);
      }
    } else if (term1 instanceof Struct<?> s1) {
      // Case of Struct <-> Var: already taken care of by switching, see above
      if (!(term2 instanceof Struct<?> s2)) {
        // Not unified - we can only unify 2 Struct
        return null;
      }
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


  /**
   * Unify against DataFact
   *
   * @param term1
   * @param dataFact
   * @return
   */
  public UnifyContext unify(Object term1, DataFact dataFact) {
    if (!(term1 instanceof Struct<?> struct)) {
      // Only Struct could match a DataFact
      return null;
    }
    final Object[] dataFactElements = dataFact.getElements();
    if (struct.getName() != dataFactElements[0]) {// Names are {@link String#intern()}alized so OK to check by reference
      // Functor must match
      return null;
    }
    final int arity = struct.getArity();
    if (arity != dataFactElements.length - 1) {
      // Arity must match as well
      return null;
    }
    final Object[] structArgs = struct.getArgs();
    UnifyContext runningMonad = this;
    // Unify all dataFactElements
    for (int i = 0; i < arity; i++) {
      final Object structArg = structArgs[i];
      final Object dataFactElement = dataFactElements[1 + i];
      runningMonad = runningMonad.unify(structArg, dataFactElement);
      if (runningMonad == null) {
        // Struct sub-dataFactElement not unified - fail the whole unification
        return null;
      }
    }
    return runningMonad;
  }

  // --------------------------------------------------------------------------
  // Reify values
  // --------------------------------------------------------------------------

  /**
   * Recursively dereference a {@link Var}iable until a constant, or free variable is reached, and return it.
   * In principle one must use the recursive form reify(Object)
   *
   * @param theVar
   * @return The dereferenced content of theVar, or theVar if it was free
   */
  private Object reifiedVar(Var<?> theVar) {
    return this.stateStorage.dereference(theVar, this.currentTransaction);
  }

  /**
   * Recursively resolve one {@link Var}iable, or a structure containing variables,
   * until a constant, or free variable is reached, and return it.
   *
   * @param term
   * @return The dereferenced content of term, or theVar if it was free, or null if term is null
   */
  public Object reify(Object term) {
    if (termApi().isFreeVar(term)) {
      term = reifiedVar((Var<?>) term);
      // The var might end up on a Struct, that needs recursive reification
    }
    if (term instanceof Struct<?> s) {
      //            audit.info("Reify Struct at t={}  {}", this.currentTransaction, term);
      if (s.getIndex() == 0) {
        // Structure is an atom or a constant term - no need to further transform
        return term;
      }
      // Structure has arguments
      final Object[] reifiedArgs = Arrays.stream(s.getArgs()).map(this::reify).toArray(Object[]::new);
      final Struct<?> res = s.cloneWithNewArguments(reifiedArgs);
      if (s.getIndex() > 0) {
        // The original structure had variables, maybe the cloned one will still have (if those were free)
        // We need to reassign indexes. It's costly, unfortunately.
        termApi().assignIndexes(res, 0);
      }
      //            audit.info("               yields {}", res);
      return res;
    }
    return term;
  }


  // ------------------------------------------------------
  // Accessors
  // ------------------------------------------------------

  public Solver getSolver() {
    return this.solver;
  }

  public SolutionListener getSolutionListener() {
    return this.solutionListener;
  }

  @Override
  public String toString() {
    return "vars#" + this.currentTransaction + stateStorage.toString();
  }

}
