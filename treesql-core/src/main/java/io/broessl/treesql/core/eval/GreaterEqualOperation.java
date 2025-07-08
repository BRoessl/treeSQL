package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeValue;

public class GreaterEqualOperation extends StackOperation {
  @Override
  public String getSymbol() {
    return ">=";
  }

  @Override
  public int getArgumentSize() {
    return 2;
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    if (arguments == null || arguments.length != 2) {
      throw new IllegalArgumentException("GreaterEqualOperation requires exactly 2 arguments");
    }
    TreeValue a = arguments[0];
    TreeValue b = arguments[1];
    boolean result = a.compareTo(b) >= 0;
    return new TreeBool(result);
  }
}
