package org.logic2j.engine.predicates.impl.math;

import org.logic2j.engine.exception.SolverException;
import org.logic2j.engine.model.SimpleBinding;
import org.logic2j.engine.model.Var;
import org.logic2j.engine.predicates.impl.FOPredicate;
import org.logic2j.engine.solver.Continuation;
import org.logic2j.engine.solver.listener.SolutionListener;
import org.logic2j.engine.unify.UnifyContext;

import java.util.Arrays;


/**
 * Successor value.
 */
public class Succ extends FOPredicate {
  protected static final Object[] EMPTY_ARRAY = new Object[0];

  public Succ(Object n0, Object n1) {
    super("succ", n0, n1);
  }

  @Override
  public Integer invokePredicate(SolutionListener theListener, UnifyContext currentVars) {
    final Object n0 = currentVars.reify(getArg(0));
    final Object n1 = currentVars.reify(getArg(1));

    if (isConstant(n0)) {
      if (isConstant(n1)) {
        for (Object c0 : constants(n0)) {
          for (Object c1 : constants(n1)) {
            // Both bound values - check
            final Object image = image(c0);
            final boolean equals = c1.equals(image);
            final int cont = notifySolutionIf(equals, theListener, currentVars);
            if (cont != Continuation.CONTINUE) {
              return cont;
            }
          }
        }
        return Continuation.CONTINUE;
      } else {
        // n1 is free, just unify in forward direction
        final Object[] images = Arrays.stream(constants(n0)).map(this::image).toArray(Object[]::new);
        return unifyAndNotifyMany(theListener, currentVars, n1, images);
      }
    }

    if (isFreeVar(n0)) {
      // n0 is a free variable, unify in reverse direction
      if (isConstant(n1)) {
        final Object[] preimages = Arrays.stream(constants(n1)).map(this::preimage).toArray(Object[]::new);
        return unifyAndNotifyMany(theListener, currentVars, n0, preimages);
      } else {
        // Two free variables - no solution
        return Continuation.CONTINUE;
      }
    }
    throw new SolverException("Should never be here");
  }


  // TODO: some predicates may generate 0 or multiple images
  protected Object image(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Integer) {
      return ((Integer) value) + 1;
    }
    if (value instanceof Long) {
      return ((Long) value) + 1L;
    }
    if (value instanceof Double) {
      return ((Double) value) + 1.0;
    }
    if (value instanceof Float) {
      return ((Float) value) + 1.0f;
    }
    throw new IllegalArgumentException("Forward method for " + this + " cannot handle argument " + value + " of " + value.getClass());
  }

  // TODO: some predicates may generate 0 or multiple preimates (eg. Abs(X, Y))
  protected Object preimage(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Integer) {
      return ((Integer) value) - 1;
    }
    if (value instanceof Long) {
      return ((Long) value) - 1L;
    }
    if (value instanceof Double) {
      return ((Double) value) - 1.0;
    }
    if (value instanceof Float) {
      return ((Float) value) - 1.0f;
    }
    throw new IllegalArgumentException("Reverse method for " + this + " cannot handle argument " + value + " of " + value.getClass());
  }


  protected Object[] constants(Object reified) {
    if (reified == null || isFreeVar(reified)) {
      return EMPTY_ARRAY;
    }
    if (reified instanceof SimpleBinding<?>) {
      return ((SimpleBinding<?>) reified).values();
    }
    // Other object: will be a scalar
    return new Object[] {reified};
  }

  /**
   * @param reified Result of {@link UnifyContext#reify(Object)}
   * @return true if reified is not a {@link Var}, including true when reified is null
   */
  protected boolean isConstant(Object reified) {
    return !(reified instanceof Var);
  }

  protected boolean isFreeVar(Object reified) {
    return reified instanceof Var;
  }
}
