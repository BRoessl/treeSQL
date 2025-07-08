package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeValue;

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
  public TreeValue call(TreeValue[] arguments) {
    if (arguments == null || arguments.length != 2) {
      throw new IllegalArgumentException("MAX Operation requires exactly 2 arguments");
    }
    TreeValue a = arguments[0];
    TreeValue b = arguments[1];
    return a.compareTo(b) >= 0 ? a : b;
  }
}
