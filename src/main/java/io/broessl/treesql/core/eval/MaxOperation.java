package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreePrimitive;

public class MaxOperation extends StackOperation {

    @Override
    public String getSymbol() {
        return "MAX";
    }

    @Override
    public int getArgumentSize() {
        return 2;
    }

    @Override
    public TreePrimitive call(TreePrimitive[] arguments) {
        if (arguments == null || arguments.length != 2) {
            throw new IllegalArgumentException("MAX Operation requires exactly 2 arguments");
        }
        TreePrimitive a = arguments[0];
        TreePrimitive b = arguments[1];
        return a.compareTo(b) >= 0 ? a : b;
    }

}
