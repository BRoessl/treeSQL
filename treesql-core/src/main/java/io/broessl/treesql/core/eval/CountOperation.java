package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeValue;

public class CountOperation extends StackAggregateOperation {

  @Override
  public String getSymbol() {
    return "COUNT";
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    // TODO use first argument as string to decide mode "how to count"
    // by type? NULL, "TYPE"...
    throw new UnsupportedOperationException();
  }
}
