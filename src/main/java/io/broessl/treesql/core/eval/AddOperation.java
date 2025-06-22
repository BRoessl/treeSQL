package io.broessl.treesql.core.eval;

import java.math.BigDecimal;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;

public class AddOperation extends StackOperation {
    @Override
    public String getSymbol() {
        return "+";
    }

    @Override
    public int getArgumentSize() {
        return 2;
    }

    @Override
    public TreePrimitive call(TreePrimitive[] arguments) {
        if (arguments == null || arguments.length != 2) {
            throw new IllegalArgumentException("AddOperation requires exactly 2 arguments");
        }
        TreePrimitive a = arguments[0];
        TreePrimitive b = arguments[1];
        if (a instanceof TreeNumber numA && b instanceof TreeNumber numB) {
            // Add numbers using BigDecimal's add method
            BigDecimal result = numA.nativeValue().add(numB.nativeValue());
            return new TreeNumber(result);
        } else if (a instanceof TreeString || b instanceof TreeString) {
            // Concatenate strings
            String result = a.nativeValue().toString() + b.nativeValue().toString();
            return new TreeString(result);
        } else {
            throw new IllegalArgumentException("AddOperation only supports numbers or strings");
        }
    }
}
