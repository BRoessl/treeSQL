package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeValue;

public class AndOperation extends StackOperation {
  @Override
  public String getSymbol() {
    return "AND";
  }

  @Override
  public int getArgumentSize() {
    return 2;
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    if (arguments == null || arguments.length != 2) {
      throw new IllegalArgumentException("AndOperation requires exactly 2 arguments");
    }
    TreeValue a = arguments[0];
    TreeValue b = arguments[1];
    if (a instanceof TreeBool boolA && b instanceof TreeBool boolB) {
      return new TreeBool(boolA.getValue() && boolB.getValue());
    } else {
      throw new IllegalArgumentException("AndOperation only supports booleans");
    }
  }
}
