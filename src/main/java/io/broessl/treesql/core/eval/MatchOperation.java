package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;

public class MatchOperation extends StackOperation {
    @Override
    public String getSymbol() {
        return "MATCH";
    }

    @Override
    public int getArgumentSize() {
        return 2;
    }

    @Override
    public TreePrimitive call(TreePrimitive[] arguments) {
        if (arguments == null || arguments.length != 2) {
            throw new IllegalArgumentException("MatchOperation requires exactly 2 arguments");
        }
        TreePrimitive a = arguments[0];
        TreePrimitive b = arguments[1];
        if (a instanceof TreeString strA && b instanceof TreeString strB) {
            boolean result = strA.nativeValue().matches(strB.nativeValue());
            return new TreeBool(result);
        } else {
            throw new IllegalArgumentException("MatchOperation requires two strings (value, regex)");
        }
    }
}
