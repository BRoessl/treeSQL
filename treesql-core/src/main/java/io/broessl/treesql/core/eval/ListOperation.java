package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeValue;
import java.util.Arrays;

public class ListOperation extends StackAggregateOperation {

  @Override
  public String getSymbol() {
    return "LIST";
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    return new TreeList(Arrays.asList(arguments));
  }
}
