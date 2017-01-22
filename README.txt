Changes from logic2j:

Term:
  nothing

Var:
  nothing

Struct:
- remove all handling of prolog lists and conversion of lists to java lists
- removed PrimitiveInfo
- no longer final
- Adding PrimitiveType, and adding the DATA value

TermApi:
- remove handling of prolog lists
- removed evaluate()
- removed
- removed normalization signature with LibraryContent
- valueOf: removed second argument FactoryMode (using always non ATOM)
- removed selectTerm()


Improvements DONE:
- NotListener -> FirstSolutionListener
- order of args in invokePredicate() (always Listener, UnifyContext)
- Removed SolutionListenerBase, using default method on interface
- Removed MutliResult, using directly Iterator<UnifyContext>
- Removed PrimitiveType, using instanceof

TODO
- Listener should become consumers
- Fix LGPL header message, update copyright date


CANNOT DO / REQUIRES STUDY
- Continuation should become and enum, and two types of ABORT: user ABORT or cancellation of enumeration, and boolean checkers.
  However the cutLevel is used to abort execution currently uses an integer.
  In the solver we have code like:
                      public Continuation onSolution(UnifyContext currentVars) {
                          ...
                          final Integer continuationFromSubGoal = solveGoalRecursive(rhs, andingListeners[nextIndex], currentVars, cutLevel);
                          return continuationFromSubGoal;
                      }
   Clearly onSolution needs to return more than just 2 possible values.