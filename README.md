# logic2j-engine
A first-order logic inference engine for Java; the engine underlying logic2j

## Goals


## Comparison of logic2j-engine to logic2j prolog engine

In the engine, there is no notion of a "theory" (a file containing facts and rules in plain text, using Prolog syntax).
The absence of "theories" implies there is no "database" features: no storage, no indexes, no lookup.
There is no inference predicate ":-" used by the solver. The solver cannot infer on rules, it can only solve expressions.
There is no "library" (a set of predicates, e.g. for IO, math, list processing, etc. implemented either in Prolog or Java).
There is no marshalling / unmarshalling of data structures from and to Prolog syntax represented as text.
Strong typing is introduced via the Binding<T> interface
Constant terms can be provided as scalar or vectors (multi-valued), via streams, iterators, arrays, collections, etc.
