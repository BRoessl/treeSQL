package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreePrimitive;

public class NotInOperation extends StackOperation {
  @Override
  public String getSymbol() {
    return "NOT IN";
  }

  @Override
  public int getArgumentSize() {
    return 2;
  }

  @Override
  public TreePrimitive call(TreePrimitive[] arguments) {
    if (arguments == null || arguments.length != 2) {
      throw new IllegalArgumentException("NotInOperation requires exactly 2 arguments");
    }
    TreePrimitive a = arguments[0];
    TreePrimitive b = arguments[1];
    if (b instanceof TreeList list) {
      boolean result = list.stream().noneMatch(item -> item.equals(a));
      return new TreeBool(result);
    } else {
      throw new IllegalArgumentException(
          "NotInOperation requires the second argument to be a TreeList");
    }
  }
}
