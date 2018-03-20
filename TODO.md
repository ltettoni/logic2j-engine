# Improvements DONE
- NotListener -> FirstSolutionListener
- order of args in invokePredicate() (always Listener, UnifyContext)
- Removed SolutionListenerBase, using default method on interface
- Removed MutliResult, using directly Iterator<UnifyContext>
- Removed PrimitiveType, using instanceof
- Fix LGPL header message, update copyright date
- Reintroduce lambda in Struct to calculate predicate logic
- SolverContext now held in UnifyContext
- Now using "Binding", and in particular SimpleBinding allows any predicate to specify multiple (constant) values in various ways
- Predicate signatures (Integer, Integer..., Var<Integer>) to the power^N... See if we can use Binding<> everywhere
- count()
- Infinite streams in SimpleBindings


# Improvements TODO
- What is the behaviour when passing free vars when not permitted (eg. LT(_, _) : no solution? exception?)
- Serious issue with BoundVars. currently a supply() predicate is instantiated at beginning of solve.
   Very inefficient if a select() is used later on since it will receive N values in sequence :-(
- Should we rather use Splititerator instead of our internal cooking with Constant<T>?
- partial reify that just goes to the first non-Var? (without solving recursively ?)
- limit(min, max) and the LimitSolutionListener
- better define aggregation: exists(goal, result), sum(), max(), min(). Probably count() and limit() are different beasts.
- Can and() and or() be implemented in their own predicates not in the Solver? at the cost of sharing which methods / state?
- Functors
- Reintroduce TermApi.selectTerm() ???
- Predicates binding real data tuples - reintroduce DataFact ????
- Does the cut (!) work in the context of or(), or only in the context of inference???
- Solution retrieval API. See https://www.jooq.org/doc/3.9/manual/sql-execution/fetching/
  - sync or asnyc ???
  - all in memory or cursor style ???  (JooQ's Cursor: Iterable<R>, AutoCloseable)
  - exists
  - count
  - count distinct
  - single (or null)  (in JooQ: fetchAny)
  - unique (non null) (in JooQ: fetchOne)
  - limited (with policy)
  - row handler
  - iterator
  - var has any value (exists not null)
  - var count (non null)
  - var count distinct
  - var stream
    - list
    - array
    - set
    - (any filtering or transformation of)
  - list of array
  - list of tuple
  - list of map
- Naming of SolutionListener.onSolution(): rather Consumer.accept() or Observer.notify() or Subscriber.process() ?
    --> Listener has a strong async flavour are we are not async at all!


# CANNOT DO / REQUIRES STUDY
- Continuation should become and enum, and two types of ABORT: user ABORT or cancellation of enumeration, and boolean checkers.
  However the cutLevel is used to abort execution currently uses an integer.
  In the solver we have code like:
```
public Continuation onSolution(UnifyContext currentVars) {
    ...
    final Integer continuationFromSubGoal = solveGoalRecursive(rhs, andingListeners[nextIndex], currentVars, cutLevel);
    return continuationFromSubGoal;
}
```
-  Clearly onSolution needs to return more than just 2 possible values.
