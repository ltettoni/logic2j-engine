package org.logic2j.predsolver.predicates.impl;

import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.solver.Continuation;
import org.logic2j.predsolver.solver.listener.SolutionListener;
import org.logic2j.predsolver.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Emit Java values into (free) variables.
 */
public class Supply<T, CT> extends FOPredicate {
  private static final Logger logger = LoggerFactory.getLogger(Supply.class);

  private final Iterator<T> iterator;

  public Supply(Iterator<T> iterator, Var<T> var) {
    super("supply", var);
    this.iterator = iterator;
  }


  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object reified = currentVars.reify(getArg(0));
//    final CT javaValues = getter.get();
//    final Collection coll = (Collection) javaValues;
    if (reified instanceof Var) {
      // Still a free var, we will attempt to read values from the getter and provide bindings

      if(iterator!=null) {
        return unifyAndNotifyMany(theListener, currentVars, (Var)reified, iterator);
      }
      return Continuation.CONTINUE;
    } else {
      // Variable is bound to a value

      if(iterator!=null) {
        Set<Object> set = new HashSet<>();
        while (iterator.hasNext()) {
          set.add(iterator.next());
        }
        return notifySolutionIf(set.contains(reified), theListener, currentVars);
      } else {
        logger.warn("Cannot store instant value {}", reified);
        return Continuation.CONTINUE;
      }
    }
  }
}
