package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;

public class NotMatchOperation extends StackOperation {
    @Override
    public String getSymbol() {
        return "NOT MATCH";
    }

    @Override
    public int getArgumentSize() {
        return 2;
    }

    @Override
    public TreePrimitive call(TreePrimitive[] arguments) {
        if (arguments == null || arguments.length != 2) {
            throw new IllegalArgumentException("NotMatchOperation requires exactly 2 arguments");
        }
        TreePrimitive a = arguments[0];
        TreePrimitive b = arguments[1];
        if (a instanceof TreeString strA && b instanceof TreeString strB) {
            boolean result = !strA.nativeValue().matches(strB.nativeValue());
            return new TreeBool(result);
        } else {
            throw new IllegalArgumentException("NotMatchOperation requires two strings (value, regex)");
        }
    }
}
