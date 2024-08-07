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

package org.logic2j.engine.solver.holder;

import org.logic2j.api.result.ResultsHolder;
import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.solver.extractor.*;
import org.logic2j.engine.solver.listener.IterableSolutionListener;
import org.logic2j.engine.solver.listener.MultiVarSolutionListener;
import org.logic2j.engine.solver.listener.RangeSolutionListener;
import org.logic2j.engine.solver.listener.SingleVarSolutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.logic2j.engine.model.TermApiLocator.termApi;

/**
 * Launch the solver with appropriate SolutionListener to obtain what the user asks:
 * cardinality (single-value or multi-valued)
 * obligation (optional, mandatory result, at-least or at-most values)
 * modality (iterable or all-in-memory)
 * structure (List or Array)
 */
public class SolutionHolder<T> implements ResultsHolder<T> {
  private static final Logger logger = LoggerFactory.getLogger(SolutionHolder.class);

  private final GoalHolder goalHolder;

  private RangeSolutionListener rangeListener;

  private long minNbr = 0;

  private long maxNbr = Long.MAX_VALUE - 1; // Keep possibility to add one because the RangeSolutionListener "fetch" property

  // I think this could be clarified a little - those are impl of the same interface, but one is generic the other not
  private final SingleVarExtractor<T> singleVarExtractor;

  private final SolutionExtractor multiVarExtractor;

  /**
   * Extract one particular variable or the solution to the goal.
   *
   * @param goalHolder
   * @param varName
   * @param desiredTypeOfResult
   * @param termToSolutionFunction
   */
  public SolutionHolder(GoalHolder goalHolder, String varName, Class<? extends T> desiredTypeOfResult,
                        BiFunction<Object, Class, Object> termToSolutionFunction) {
    this.goalHolder = goalHolder;
    this.singleVarExtractor = new SingleVarExtractor<>(goalHolder.effectiveGoal(), varName, desiredTypeOfResult);
    this.singleVarExtractor.setTermToSolutionFunction(termToSolutionFunction);
    this.multiVarExtractor = null;
  }

  private SolutionHolder(GoalHolder goalHolder, SolutionExtractor<?> extractor) {
    this.goalHolder = goalHolder;
    this.singleVarExtractor = null;
    this.multiVarExtractor = extractor;
  }

  /**
   * Factory: extract all variables in Maps (the not-so-efficient way)
   *
   * @param goalHolder
   * @return Holds solutions as a List of Maps
   */
  public static SolutionHolder<Map<Var<?>, Object>> extractingMaps(GoalHolder goalHolder) {
    return new SolutionHolder<>(goalHolder, new MapExtractor(goalHolder.effectiveGoal()));
  }

  /**
   * Factory: extract all variables in Maps (the not-so-efficient way)
   *
   * @param goalHolder
   * @param vars The variables of interest
   * @return Holds solutions as a List of Maps
   */
  public static SolutionHolder<Map<Var<?>, Object>> extractingMaps(GoalHolder goalHolder, Var<?>... vars) {
    return new SolutionHolder<>(goalHolder, new MapExtractor(vars));
  }

  /**
   * Factory: extract all variables in Arrays (the efficient way)
   *
   * @param goalHolder
   * @return Holds solutions as a List of Arrays
   */
  public static SolutionHolder<Object[]> extractingArrays(GoalHolder goalHolder) {
    return new SolutionHolder<>(goalHolder, new ArrayExtractor(goalHolder.effectiveGoal()));
  }

  /**
   * Factory: extract all variables in Arrays (the efficient way)
   * @param goalHolder
   * @param vars The variables of interest
   * @return Holds solutions as a List of Arrays
   */
  public static SolutionHolder<Object[]> extractingArrays(GoalHolder goalHolder, Var<?>... vars) {
    return new SolutionHolder<>(goalHolder, new ArrayExtractor(vars));
  }

  public static <T> SolutionHolder<T> extractingFactory(GoalHolder goalHolder, ObjectFactory<T> factory) {
    return new SolutionHolder<>(goalHolder, new FactoryExtractor<>(goalHolder.effectiveGoal(), factory));
  }


  // ---------------------------------------------------------------------------
  // Scalar extractors (zero or one solution)
  // ---------------------------------------------------------------------------

  /**
   * Launches the solver.
   *
   * @return The only solution or null if none, but will throw an Exception if more than one.
   */
  @Override
  public Optional<T> single() {
    initListenerRangesAndSolve(0, 1, 2);
    if (rangeListener.getNbSolutions() == 0) {
      return Optional.empty();
    }
    return Optional.ofNullable((T) rangeListener.getResults().getFirst());
  }

  /**
   * Launches the solver.
   *
   * @return The first solution, or null if none. Will not generate any further - there may be or not - you won't notice.
   */
  @Override
  public Optional<T> first() {
    initListenerRangesAndSolve(0, 1, 1);
    if (rangeListener.getNbSolutions() == 0) {
      return Optional.empty();
    }
    return Optional.ofNullable((T)rangeListener.getResults().getFirst());
  }

  /**
   * Launches the solver.
   *
   * @return Single and only solution. Will throw a RuntimeException if zero or more than one.
   */
  @Override
  public T unique() {
    initListenerRangesAndSolve(1, 1, 2);
    return (T) rangeListener.getResults().getFirst();
  }


  /**
   * There is one single solution, but it is the free Variable (unbound).
   *
   * @return true if solution is not bound to a literal term.
   */
  public boolean isFree() {
    return termApi().isFreeVar(unique());
  }


  // ---------------------------------------------------------------------------
  // Vector extractors (collections, arrays, iterables)
  // ---------------------------------------------------------------------------

  /**
   * Launches the solver.
   *
   * @return an ordered List of solutions.
   */
  @Override
  public List<T> list() {
    initListenerRangesAndSolve(this.minNbr, this.maxNbr, this.maxNbr + 1);
    return (List<T>) rangeListener.getResults();
  }


  /**
   * Launches the solver and collect solutions into a user-specified Collection
   *
   * @param theTargetToAddTo The target collection (with user-chosen semantics) where all solutions should be added to.
   * @return the argument "theTargetToAddTo"
   */
  @Override
  public <Q extends Collection<T>> Q addTo(Q theTargetToAddTo) {
    // We could have used "this" (which is an Iterable) instead of list(), but this uses two threads and is less efficient
    // than storing all in memory
    theTargetToAddTo.addAll(list());
    return theTargetToAddTo;
  }


  /**
   * Launches the solver.
   *
   * @return a distinct Set of solutions in a HashSet.
   * @note This is an inefficient implementation: all solutions are gathered first using List(), and then added individually
   * into a HashSet. A much better approach would be to calculate the "distinct" feature where
   * SolutionExtractor.extractSolution() is called, ie in one of the various
   */
  @Override
  public Set<T> set() {
    return new HashSet<>(list());
  }


  /**
   * Launches the solver.
   *
   * @param destinationArray Array to be populated or reallocated.
   * @return The resulting array.
   */
  @Override
  public T[] array(T[] destinationArray) {
    return list().toArray(destinationArray);
  }


  /**
   * Launch the prolog engine in a separate thread to produce solutions while the main caller can consume
   * from this {@link Iterator} at its own pace.
   * This uses the {@link IterableSolutionListener}.
   * Note: there is no bounds checking when using iterator()
   *
   * @return An iterator for all solutions.
   */
  @Override
  public Iterator<T> iterator() {
    final SolutionExtractor<?> effectiveExtractor;
    if (SolutionHolder.this.singleVarExtractor != null) {
      effectiveExtractor = SolutionHolder.this.singleVarExtractor;
    } else {
      effectiveExtractor = SolutionHolder.this.multiVarExtractor;
    }
    final IterableSolutionListener listener = new IterableSolutionListener(effectiveExtractor);

    final Runnable prologSolverThread = () -> {
      logger.debug("Started producer (prolog solver engine) thread");
      // Start solving in a parallel thread, and rush to first solution (that will be called back in the listener)
      // and will wait for the main thread to extract it
      SolutionHolder.this.goalHolder.getSolver().solveGoal(SolutionHolder.this.goalHolder.effectiveGoal(), listener);
      logger.debug("Producer (prolog solver engine) thread finishes");
      // Last solution was extracted. Producer's callback won't now be called anymore - so to
      // prevent the consumer for listening forever for the next solution that won't come...
      // We wait from a last notify from our client
      listener.clientToEngineInterface().waitUntilAvailable();
      // And we tell it we are aborting. No solution transferred for this last "hang up" message
      listener.engineToClientInterface().wakeUp();
      // Notice the 2 lines above are exactly the sames as those in the listener's onSolution()
    };
    new Thread(prologSolverThread).start();

    return new Iterator<>() {

      private Object solution;

      @Override
      public boolean hasNext() {
        // Now ask engine to run...
        listener.clientToEngineInterface().wakeUp();
        // And wait for a solution. Store it in any case we need it in next()
        this.solution = listener.engineToClientInterface().waitUntilAvailable();
        // Did it get one?
        return this.solution != null;
      }

      @Override
      public T next() {
        if (this.solution == null) {
          throw new NoSuchElementException(
                  "Program error: next() called when either hasNext() did not return true previously, or next() was called more than once");
        }
        final Object toReturn = this.solution;
        // Indicate that we have just "consumed" the solution, and any subsequent call to next() without first calling hasNext()
        // will fail.
        this.solution = null;
        return (T) toReturn;
      }

      @Override
      public void remove() {
        throw new SolverException("iterator() provides a read-only Term iterator, cannot remove elements");
      }

    };
  }

  // ---------------------------------------------------------------------------
  // Enforcement of cardinality
  // ---------------------------------------------------------------------------

  /**
   * Specify that the number of solutions to be extracted by this SolutionHolder
   * must be exactly as specified
   *
   * @param expectedNumberOfSolutions
   * @return
   * @note Does not apply to #iterator().
   */
  @Override
  public SolutionHolder<T> exactly(int expectedNumberOfSolutions) {
    return atLeast(expectedNumberOfSolutions).atMost(expectedNumberOfSolutions);
  }


  /**
   * Specify that the number of solutions to be extracted by this SolutionHolder
   * must at be at least minimalNumberOfSolutions.
   *
   * @param minimalNumberOfSolutions
   * @return this instance
   * @note Does not apply to #iterator().
   */
  @Override
  public SolutionHolder<T> atLeast(int minimalNumberOfSolutions) {
    this.minNbr = minimalNumberOfSolutions;
    return this;
  }

  /**
   * Specify that the number of solutions to be extracted by this SolutionHolder
   * must at be at most minimalNumberOfSolutions.
   *
   * @param maximalNumberOfSolutions
   * @return this instance
   * @note Does not apply to #iterator().
   */
  @Override
  public SolutionHolder<T> atMost(int maximalNumberOfSolutions) {
    this.maxNbr = maximalNumberOfSolutions;
    return this;
  }


  // ---------------------------------------------------------------------------
  // Interface ResultsHolder
  // ---------------------------------------------------------------------------

  public <R> ResultsHolder<R> var(Var<R> variable) {
    return null;
  }

  @Override
  public <R> ResultsHolder<R> map(Function<T, R> mapping) {
    throw new UnsupportedOperationException("Not implemented");
  }


  // ---------------------------------------------------------------------------
  // Support methods
  // ---------------------------------------------------------------------------

  private void initListenerRangesAndSolve(long minCount, long maxCount, long maxFetch) {
    if (this.singleVarExtractor != null) {
      this.rangeListener = new SingleVarSolutionListener(this.singleVarExtractor);
    } else {
      assert this.multiVarExtractor != null : "neither single nor multiple var extractor";
      this.rangeListener = new MultiVarSolutionListener(this.multiVarExtractor);
    }
    this.rangeListener.setMinCount(minCount);
    this.rangeListener.setMaxCount(maxCount);
    this.rangeListener.setMaxFetch(maxFetch);
    solveAndCheckRanges();
  }

  private void solveAndCheckRanges() {
    this.goalHolder.getSolver().solveGoal(this.goalHolder.effectiveGoal(), this.rangeListener);
    this.rangeListener.checkRange();
  }


  @Override
  public String toString() {
    return this.getClass().getSimpleName() + '(' + this.goalHolder + ')';
  }

}
