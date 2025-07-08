package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeValue;
import java.math.BigDecimal;

public class MultiplyOperation extends StackOperation {
  @Override
  public String getSymbol() {
    return "*";
  }

  @Override
  public int getArgumentSize() {
    return 2;
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    if (arguments == null || arguments.length != 2) {
      throw new IllegalArgumentException("MultiplyOperation requires exactly 2 arguments");
    }
    TreeValue a = arguments[0];
    TreeValue b = arguments[1];
    if (a instanceof TreeNumber numA && b instanceof TreeNumber numB) {
      BigDecimal result = numA.getValue().multiply(numB.getValue());
      return new TreeNumber(result);
    } else {
      throw new IllegalArgumentException("MultiplyOperation only supports numbers");
    }
  }
}
