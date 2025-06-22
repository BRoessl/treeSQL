package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreePrimitive;

public class NotOperation extends StackOperation {
    @Override
    public String getSymbol() {
        return "NOT";
    }

    @Override
    public int getArgumentSize() {
        return 1;
    }

    @Override
    public TreePrimitive call(TreePrimitive[] arguments) {
        if (arguments == null || arguments.length != 1) {
            throw new IllegalArgumentException("NotOperation requires exactly 1 argument");
        }
        TreePrimitive a = arguments[0];
        if (a instanceof TreeBool boolA) {
            return new TreeBool(!boolA.nativeValue());
        } else {
            throw new IllegalArgumentException("NotOperation only supports booleans");
        }
    }
}
