package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeValue;
import java.math.BigDecimal;
import java.util.List;

public class MinOperation extends StackAggregateOperation {

  @Override
  public String getSymbol() {
    return "MIN";
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    List<TreeValue> flatArguments = flatArguments(arguments);
    TreeNumber min = null;
    for (TreeValue treeValue : flatArguments) {
      if (treeValue instanceof TreeNumber treeNum) {
        min = min != null ? min : treeNum;
        min = min.compareTo(treeNum) < 0 ? min : treeNum;
      } else if (treeValue instanceof TreeBool treeBool) {
        TreeNumber asNumber =
            treeBool.getValue() ? new TreeNumber(BigDecimal.ONE) : new TreeNumber(BigDecimal.ZERO);
        min = min != null ? min : asNumber;
        min = min.compareTo(asNumber) < 0 ? min : asNumber;
      } else {
        // ignore
      }
    }
    if (min == null) {
      return TreeNull.INSTANCE;
    }
    return min;
  }
}
