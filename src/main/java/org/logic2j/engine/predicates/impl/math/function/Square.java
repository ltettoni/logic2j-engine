package org.logic2j.engine.predicates.impl.math.function;

import org.logic2j.engine.model.Binding;
import org.logic2j.engine.predicates.impl.math.Pred2;

public class Square extends Pred2<Integer, Integer> {
    public Square(Binding<Integer> arg0, Binding<Integer> arg1) {
        super("square", arg0, arg1);
        setPreimage(val -> (int) Math.sqrt(val));
        setImage(val -> val * val);
    }
}
