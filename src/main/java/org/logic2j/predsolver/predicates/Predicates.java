package org.logic2j.predsolver.predicates;

import org.logic2j.predsolver.model.Term;
import org.logic2j.predsolver.model.Var;
import org.logic2j.predsolver.predicates.impl.Eq;
import org.logic2j.predsolver.predicates.impl.Fail;
import org.logic2j.predsolver.predicates.impl.True;
import org.logic2j.predsolver.predicates.internal.And;
import org.logic2j.predsolver.predicates.internal.Cut;
import org.logic2j.predsolver.predicates.internal.Or;

/**
 * Factory methods for common predicates.
 */
public final class Predicates {

  public static final True ttrue = new True();
  public static final Fail fail = new Fail();
  public static final Cut cut = new Cut();
  public static final Var<Void> anonymous = Var.ANONYMOUS_VAR;

  public static And and(Term... conjunctions) {
    return new And(conjunctions);
  }

  public static Or or(Term... disjunctions) {
    return new Or(disjunctions);
  }

  public static Eq eq(Object t1, Object t2) {
    return new Eq(t1, t2);
  }

}
