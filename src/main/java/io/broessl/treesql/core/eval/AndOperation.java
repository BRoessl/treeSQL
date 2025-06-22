package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreePrimitive;

public class AndOperation extends StackOperation {
    @Override
    public String getSymbol() {
        return "AND";
    }

    @Override
    public int getArgumentSize() {
        return 2;
    }

    @Override
    public TreePrimitive call(TreePrimitive[] arguments) {
        if (arguments == null || arguments.length != 2) {
            throw new IllegalArgumentException("AndOperation requires exactly 2 arguments");
        }
        TreePrimitive a = arguments[0];
        TreePrimitive b = arguments[1];
        if (a instanceof TreeBool boolA && b instanceof TreeBool boolB) {
            return new TreeBool(boolA.nativeValue() && boolB.nativeValue());
        } else {
            throw new IllegalArgumentException("AndOperation only supports booleans");
        }
    }
}
