package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeValue;
import java.math.BigDecimal;
import java.util.List;

public class SumOperation extends StackAggregateOperation {

  @Override
  public String getSymbol() {
    return "SUM";
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    List<TreeValue> flatArguments = flatArguments(arguments);
    BigDecimal sum = null;
    for (TreeValue treeValue : flatArguments) {
      if (treeValue instanceof TreeNumber treeNum) {
        sum = sum != null ? sum.add(treeNum.getValue()) : treeNum.getValue();
      } else if (treeValue instanceof TreeBool treeBool) {
        sum =
            sum != null
                ? sum.add(treeBool.getValue() ? BigDecimal.ONE : BigDecimal.ZERO)
                : treeBool.getValue() ? BigDecimal.ONE : BigDecimal.ZERO;
      } else {
        // ignore
      }
    }
    if (sum == null) {
      return TreeNull.INSTANCE;
    }
    return new TreeNumber(sum);
  }
}
