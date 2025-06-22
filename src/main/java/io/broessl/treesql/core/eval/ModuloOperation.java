package io.broessl.treesql.core.eval;

import java.math.BigDecimal;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;

public class ModuloOperation extends StackOperation {
    @Override
    public String getSymbol() {
        return "%";
    }

    @Override
    public int getArgumentSize() {
        return 2;
    }

    @Override
    public TreePrimitive call(TreePrimitive[] arguments) {
        if (arguments == null || arguments.length != 2) {
            throw new IllegalArgumentException("ModuloOperation requires exactly 2 arguments");
        }
        TreePrimitive a = arguments[0];
        TreePrimitive b = arguments[1];
        if (a instanceof TreeNumber numA && b instanceof TreeNumber numB) {
            if (numB.nativeValue().compareTo(BigDecimal.ZERO) == 0) {
                throw new ArithmeticException("Modulo by zero");
            }
            BigDecimal result = numA.nativeValue().remainder(numB.nativeValue());
            return new TreeNumber(result);
        } else {
            throw new IllegalArgumentException("ModuloOperation only supports numbers");
        }
    }
}
