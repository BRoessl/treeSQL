package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;

public class AsStringFunction extends StackOperation {
  @Override
  public String getSymbol() {
    return "AS_STRING";
  }

  @Override
  public int getArgumentSize() {
    return 1;
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    TreeValue convertMe = arguments[0];
    if (convertMe instanceof TreeString string) {
      return string;
    } else {
      return new TreeString(convertMe.getValue().toString());
    }
  }
}
