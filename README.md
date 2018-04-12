# logic2j-engine
A first-order logic inference engine for Java; the engine underlying logic2j

## Goals
This library provides an API to solving logical problems such as:

```
Find all digit integers (0..9) for which the reminder (base 10) of their square is equal to themselves.
4 solutions: 0, 1, 5 (=25), 6 (=36)

Var<Integer> x = intVar("X");
Var<Integer> square = intVar("S");
Term expr = new And(new Digit(x), new Square(x, square), new Mod10(square, x) /*, new Log("info", square) */);
long nbr = solver.solve(expr).count();
```

1. Defining typed variables or constants bound to Java data structures
2. Assembling terms and predicates in abstract syntax trees of logic operators
3. Solving by inference: java predicate will be invoked to find all solutions
4. Possibly binding ASTs to expression languages such as SQL and solving against a database


## Comparison of logic2j-engine to logic2j prolog engine

In the engine, there is no notion of a "theory" (a file containing facts and rules in plain text, using Prolog syntax).
The absence of "theories" implies there is no "database" features: no storage, no indexes, no lookup.
There is no inference predicate ":-" used by the solver. The solver cannot infer on rules, it can only solve expressions.
There is no "library" (a set of predicates, e.g. for IO, math, list processing, etc. implemented either in Prolog or Java).
There is no marshalling / unmarshalling of data structures from and to Prolog syntax represented as text.
Strong typing is introduced via the Binding<T> interface
Constant terms can be provided as scalar or vectors (multi-valued), via streams, iterators, arrays, collections, etc.
