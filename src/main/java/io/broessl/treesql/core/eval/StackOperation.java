package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.eval.stack.Stackable;
import io.broessl.treesql.core.types.TreePrimitive;

public abstract class StackOperation implements Stackable {

    abstract public String getSymbol();

    abstract public int getArgumentSize();

    abstract public TreePrimitive call(TreePrimitive[] arguments);

    public String toString() {
        return getSymbol();
    }

}
