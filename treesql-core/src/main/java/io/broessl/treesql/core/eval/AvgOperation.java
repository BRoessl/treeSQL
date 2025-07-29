package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeValue;
import java.math.BigDecimal;
import java.util.List;

public class AvgOperation extends StackAggregateOperation {

  @Override
  public String getSymbol() {
    return "AVG";
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    List<TreeValue> flatArguments = flatArguments(arguments);
    BigDecimal sum = BigDecimal.ZERO;
    int count = 0;
    for (TreeValue treeValue : flatArguments) {
      if (treeValue instanceof TreeNumber treeNum) {
        count++;
        sum = sum.add(treeNum.getValue());
      } else if (treeValue instanceof TreeBool treeBool) {
        count++;
        sum = sum.add(treeBool.getValue() ? BigDecimal.ONE : BigDecimal.ZERO);
      } else {
        throw new IllegalArgumentException(
            "AVG Operation is not applicable on data type " + treeValue.getClass().getSimpleName());
      }
    }
    if (count == 0) {
      return TreeNull.INSTANCE;
    }
    return new TreeNumber(sum.divide(BigDecimal.valueOf(count)));
  }
}
