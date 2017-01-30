package org.logic2j.engine.predicates.impl.math;

import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;


/**
 * Successor value.
 */
public class Succ extends FOPredicate {
  public Succ(Object n0, Object n1) {
    super("succ", n0, n1);
  }

  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object n0 = currentVars.reify(getArg(0));
    final Object n1 = currentVars.reify(getArg(1));

    final int cont;
    if (isConstant(n0)) {
      if (isConstant(n1)) {
        // Both bound values - check
        final Object image = image(n0);
        final boolean equals = n1.equals(image);
        cont = notifySolutionIf(equals, theListener, currentVars);
      } else {
        // n1 is free, just unify in forward direction
        final Object image = image(n0);
        cont =  unifyAndNotify(theListener, currentVars, n1, image);
      }
    } else {
      // n0 is a free variable, unify in reverse direction
      if (isConstant(n1)) {
        final Object preimage = preimage(n1);
        cont =  unifyAndNotify(theListener, currentVars, n0, preimage);
      } else {
        // Two free variables - no solution
        cont =  Continuation.CONTINUE;
      }
    }
    return cont;
  }

  protected Object image(Object value) {
    if (value==null) {
      return null;
    }
    if (value instanceof Integer) {
      return ((Integer)value) + 1;
    }
    if (value instanceof Long) {
      return ((Long)value) + 1L;
    }
    if (value instanceof Double) {
      return ((Double)value) + 1.0;
    }
    if (value instanceof Float) {
      return ((Float)value) + 1.0f;
    }
    throw new IllegalArgumentException("Forward method for " + this + " cannot handle argument " + value + " of " + value.getClass());
  }

  protected Object preimage(Object value) {
    if (value==null) {
      return null;
    }
    if (value instanceof Integer) {
      return ((Integer)value) - 1;
    }
    if (value instanceof Long) {
      return ((Long)value) - 1L;
    }
    if (value instanceof Double) {
      return ((Double)value) - 1.0;
    }
    if (value instanceof Float) {
      return ((Float)value) - 1.0f;
    }
    throw new IllegalArgumentException("Reverse method for " + this + " cannot handle argument " + value + " of " + value.getClass());
  }


  /**
   * @param reified Result of {@link UnifyContext#reify(Object)}
   * @return true if reified is not a {@link Var}, including true when reified is null
   */
  protected boolean isConstant(Object reified) {
    return ! (reified instanceof Var);
  }
}
