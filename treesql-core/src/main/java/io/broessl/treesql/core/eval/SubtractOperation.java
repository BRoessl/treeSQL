package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import java.math.BigDecimal;

public class SubtractOperation extends StackOperation {
  @Override
  public String getSymbol() {
    return "-";
  }

  @Override
  public int getArgumentSize() {
    return 2;
  }

  @Override
  public TreePrimitive call(TreePrimitive[] arguments) {
    if (arguments == null || arguments.length != 2) {
      throw new IllegalArgumentException("SubtractOperation requires exactly 2 arguments");
    }
    TreePrimitive a = arguments[0];
    TreePrimitive b = arguments[1];
    if (a instanceof TreeNumber numA && b instanceof TreeNumber numB) {
      BigDecimal result = numA.getValue().subtract(numB.getValue());
      return new TreeNumber(result);
    } else {
      throw new IllegalArgumentException("SubtractOperation only supports numbers");
    }
  }
}
