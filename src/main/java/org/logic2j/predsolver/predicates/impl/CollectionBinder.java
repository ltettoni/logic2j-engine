package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.solver.listener.UnifyContextIterator;
import org.logic2j.predsolver.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Bind collection of values to a Variable, for both read and write access.
 */
public class CollectionBinder<T, CT> extends FOPredicate {
  private static final Logger logger = LoggerFactory.getLogger(CollectionBinder.class);

  private final Supplier<CT> getter;
  private final Consumer<CT> setter;

  public CollectionBinder(Supplier<CT> getter, Var<T> var, Consumer<CT> setter) {
    super("collectionBinder", var);
    this.getter = getter;
    this.setter = setter;
  }


  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object reified = currentVars.reify(getArg(0));
    final CT javaValues = getter.get();
    final Collection coll = (Collection) javaValues;
    if (reified instanceof Var) {
      // Still a free var, we will attempt to read values from the getter and provide bindings

      if(coll!=null) {
        final UnifyContextIterator iterator = new UnifyContextIterator(currentVars, (Var) reified, coll);
        return theListener.onSolutions(iterator);
      }
      return Continuation.CONTINUE;
    } else {
      // Variable is bound to a value
      if (coll!=null) {
        // Java collection is define, just check memberhsip
        return notifySolutionIf(coll.contains(reified), theListener, currentVars);
      } else {

        logger.warn("Cannot store instant value {}", reified);
        return Continuation.CONTINUE;
      }
    }
  }
}
