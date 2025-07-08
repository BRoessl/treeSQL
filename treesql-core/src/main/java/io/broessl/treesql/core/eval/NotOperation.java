package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeValue;

public class NotOperation extends StackOperation {
  @Override
  public String getSymbol() {
    return "NOT";
  }

  @Override
  public int getArgumentSize() {
    return 1;
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    if (arguments == null || arguments.length != 1) {
      throw new IllegalArgumentException("NotOperation requires exactly 1 argument");
    }
    TreeValue a = arguments[0];
    if (a instanceof TreeBool boolA) {
      return new TreeBool(!boolA.getValue());
    } else {
      throw new IllegalArgumentException("NotOperation only supports booleans");
    }
  }
}
