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


import org.logic2j.engine.exception.InvalidTermException;
import org.logic2j.engine.exception.Logic2jException;
import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Struct;
import org.logic2j.engine.model.Term;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.predicates.internal.And;
import org.logic2j.engine.predicates.internal.Call;
import org.logic2j.engine.predicates.internal.Cut;
import org.logic2j.engine.predicates.internal.Or;
import org.logic2j.engine.predicates.internal.SolverPredicate;
import org.logic2j.engine.solver.holder.GoalHolder;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;
import org.logic2j.engine.util.ProfilingInfo;

import static org.logic2j.engine.model.TermApiLocator.termApi;
import static org.logic2j.engine.predicates.Predicates.and;

/**
 * Solve goals - that's the core of the engine, the resolution algorithm is in this class.
 * There are 4 predicates managed directly in this class:
 * "," (AND)
 * ";" (OR)
 * "call(X)"
 * "!" (CUT)
 * ( and in the future, ":-" (RULE) )
 * All other predicates are delegated in implementations of {@link FOPredicate#predicateLogic(UnifyContext)}.
 */
public class Solver {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Solver.class);

  /**
   * Recursion counter uses positive numbers exclusively, see algorithm described in {@link Cut}
   */
  private static final int INITIAL_CUT_LEVEL = 1;


  /**
   * Do we solve the ";" (OR) predicate internally here, or in the predicate.
   * (see note re. processing of OR in CoreLibrary.pro)
   */
  protected boolean isInternalOr() {
    return true;
  } // FIXME Still dubious re. use of internal or and its behaviour with Cut. See note in logic2j overloaded method

  /**
   * Do we acquire profiling information (number of inferences, etc)
   */
  protected boolean isProfiling() {
    return false;
  }

  /**
   * Higher-level solver, delaying execution of a conjuction (AND) of goals.
   * @param goals
   * @return The {@link GoalHolder} that delays execution until you tell what you want to extract.
   */
  public GoalHolder solve(Term... goals) {
    final Term effective = goals.length == 1 ? goals[0] : and(goals);
    final Object normalized = termApi().normalize(effective);
    return new GoalHolder(this, normalized, null);
  }

  /**
   * This is the naive, simplest entry point for solving a goal, when all variable have to be initially free.
   *
   * @param goal
   * @param solutionListener
   * @return Continuation
   */
  public int solveGoal(Object goal, SolutionListener solutionListener) {
    if (termApi().isFreeVar(goal)) {
      throw new InvalidTermException("Cannot solve the goal \"" + goal + "\", the variable is not bound to a value");
    }
    final UnifyContext initialContext = new UnifyContext(this, solutionListener);
    if (goal instanceof Struct) {
      // We will need to clone Clauses during resolution, hence the base index
      // for any new var must be higher than any of the currently used vars.
      initialContext.topVarIndex(((Struct) goal).getIndex());
    }
    try {
      return solveGoal(goal, initialContext);
    } catch (Logic2jException e) {
      // "Functional" exception thrown during solving will just be forwarded
      throw e;
    } catch (RuntimeException e) {
      // Anything not a Logic2jException will be encapsulated
      throw new SolverException("Solver failed with: " + e, e);
    }
  }

  /**
   * This is an alternate entry point when a {@link UnifyContext}
   * is already instantiated; this is needed in custom predicates implementing first-order logic like
   * not(), exists(), etc.
   * You enter here when part of the variables have been bound already.
   */
  public int solveGoal(Object goal, UnifyContext currentVars) {
    // Check if we will have to deal with DataFacts in this session of solving.
    // This slightly improves performance - we can bypass calling the method that deals with that
    if (goal instanceof Struct && !((Struct) goal).hasIndex()) {
      throw new InvalidTermException("Struct must be normalized before it can be solved: \"" + goal + "\" - call termApi().normalize()");
    }
    return solveInternalRecursive(goal, currentVars, INITIAL_CUT_LEVEL);
  }



  /**
   * That's the complex method - the heart of the Solver, it needs to be public for friend predicates such as
   * {@link And}, {@link org.logic2j.engine.predicates.internal.Call} to be able to invoke recursion.
   *
   * @param goalTerm
   * @param currentVars
   * @param cutLevel
   * @return
   */
  public int solveInternalRecursive(final Object goalTerm, final UnifyContext currentVars, final int cutLevel) {
    final long inferenceCounter = ProfilingInfo.nbInferences;
    if (logger.isDebugEnabled()) {
      logger.debug("-->> Entering solveRecursive#{}, reifiedGoal = {}", inferenceCounter, currentVars.reify(goalTerm));
      logger.debug("     cutLevel={}", cutLevel);
    }
    if (isProfiling()) {
      ProfilingInfo.nbInferences++;
    }
    int result;

    // Make sure the term specified is solvable: atoms are not, variables not (yet)
    final Struct goalStruct;
    if (goalTerm instanceof String) {
      // Yet we are not capable of handing String everywhere below - so use a Struct atom still
      goalStruct = new Struct((String) goalTerm);
        /* Prototype code - does actually not work but could
        } else if (termApi().isFreeVar(goalTerm)) {
            // Crazy we, we allow a single Var to be considered as a goal - just assuming it is bound to a Struct
            final Object goalReified = currentVars.reify(goalTerm);
            if (termApi().isFreeVar(goalReified)) {
                throw new UnsupportedOperationException("A free variable cannot be used as a goal in a rule: \"" + goalTerm + '"');
            }
            if (! (goalReified instanceof Struct)) {
                throw new UnsupportedOperationException("Vars used as a goal must always be bound to a Struct, was: \"" + goalReified + '"');
            }
            goalStruct = (Struct) goalReified;
        */
    } else {
      assert goalTerm instanceof Struct :
          "Calling solveGoalRecursive with a goal that is not a Struct but: \"" + goalTerm + "\" of " + goalTerm.getClass();
      goalStruct = (Struct) goalTerm;
    }

    // Extract all features of the goal to solve
    final String functor = goalStruct.getName();

    // First we will check the goal against core predicates such as
    // AND (","), OR (";"), CUT ("!") and CALL
    // Then we will check if the goal is a Primitive implemented in a Java library
    // Finally we will handle classic goals matched against Prolog theories

    if (goalStruct instanceof SolverPredicate) {
      result = ((SolverPredicate) goalStruct).predicateLogic(currentVars, cutLevel);
    }

    // A classic Struct with the functor representing And
    else if (Struct.FUNCTOR_COMMA == functor) { // Names are {@link String#intern()}alized so OK to check by reference
      result = And.andLogic(goalStruct, currentVars, cutLevel);
    }
    // The OR predicate
    else if (isInternalOr() && Struct.FUNCTOR_SEMICOLON == functor) { // Names are {@link String#intern()}alized so OK to check by reference
      result = Or.orLogic(goalStruct, currentVars, cutLevel);
    }
    // The CALL predicate
    else if (Struct.FUNCTOR_CALL == functor) { // Names are {@link String#intern()}alized so OK to check by reference
      result = Call.callLogic(goalStruct, currentVars, cutLevel);
    }
    // The CUT functor, all the trick is in the returned value, see comments in the Cut class.
    else if (Struct.FUNCTOR_CUT == functor) {
      result = Cut.cutLogic(currentVars, cutLevel);
    }
    // ---------------------------------------------------------------------------
    // Primitive implemented in Java
    // ---------------------------------------------------------------------------
    else if (isJava(goalStruct)) {
      result = invokeJava(goalStruct, currentVars);
    }
    //---------------------------------------------------------------------------
    // Not any "special" handling
    //---------------------------------------------------------------------------

    else {
      //---------------------------------------------------------------------------
      // Regular prolog inference rule: goal :- subGoal
      // Note: logic to handle the CUT goal here (incrementing cutLevel)
      //---------------------------------------------------------------------------
      result = solveAgainstClauseProviders(goalTerm, currentVars, cutLevel + 1);

      //---------------------------------------------------------------------------
      // Solve against data facts
      //---------------------------------------------------------------------------
      if (result == Continuation.CONTINUE) {
        result = solveAgainstDataProviders(goalTerm, currentVars);
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("<<-- Exiting  solveRecursive#" + inferenceCounter + ", reifiedGoal = {}, result={}", currentVars.reify(goalTerm), result);
    }
    return result;
  }

  protected boolean isJava(Struct goalStruct) {
    return goalStruct instanceof FOPredicate;
  }

  protected int invokeJava(Struct goal, UnifyContext currentVars) {
    final FOPredicate javaPredicate = (FOPredicate) goal;
    // The result will be the continuation code or CUT level
    return javaPredicate.predicateLogic(currentVars);
  }


  protected int solveAgainstClauseProviders(final Object goalTerm, UnifyContext currentVars, final int cutLevel) {
    return Continuation.CONTINUE;
  }

  protected int solveAgainstDataProviders(final Object goalTerm, final UnifyContext currentVars) {
    return Continuation.CONTINUE;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }

}
