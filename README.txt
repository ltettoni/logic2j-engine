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

TODO
- Listener should become consumers
- Continuation should become and enum, and two types of ABORT: user ABORT or cancellation of enumeration, and boolean checkers
- Fix LGPL header message, update copyright date


