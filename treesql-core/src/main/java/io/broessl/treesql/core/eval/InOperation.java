package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeValue;

public class InOperation extends StackOperation {
  @Override
  public String getSymbol() {
    return "IN";
  }

  @Override
  public int getArgumentSize() {
    return 2;
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    if (arguments == null || arguments.length != 2) {
      throw new IllegalArgumentException("InOperation requires exactly 2 arguments");
    }
    TreeValue a = arguments[0];
    TreeValue b = arguments[1];
    if (b instanceof TreeList list) {
      boolean result = list.stream().anyMatch(item -> item.equals(a));
      return new TreeBool(result);
    } else {
      throw new IllegalArgumentException(
          "InOperation requires the second argument to be a TreeList");
    }
  }
}
