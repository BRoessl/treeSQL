package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;

public class MatchOperation extends StackOperation {
  @Override
  public String getSymbol() {
    return "MATCH";
  }

  @Override
  public int getArgumentSize() {
    return 2;
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    if (arguments == null || arguments.length != 2) {
      throw new IllegalArgumentException("MatchOperation requires exactly 2 arguments");
    }
    TreeValue a = arguments[0];
    TreeValue b = arguments[1];
    if (a instanceof TreeString strA && b instanceof TreeString strB) {
      boolean result = strA.getValue().matches(strB.getValue());
      return new TreeBool(result);
    } else {
      throw new IllegalArgumentException("MatchOperation requires two strings (value, regex)");
    }
  }
}
