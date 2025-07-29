package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeValue;

public class FlatOperation extends StackAggregateOperation {

  @Override
  public String getSymbol() {
    return "FLAT";
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    return new TreeList(flatArguments(arguments));
  }
}
