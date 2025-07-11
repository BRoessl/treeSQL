package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import java.math.BigDecimal;

public class AddOperation extends StackOperation {
  @Override
  public String getSymbol() {
    return "+";
  }

  @Override
  public int getArgumentSize() {
    return 2;
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    if (arguments == null || arguments.length != 2) {
      throw new IllegalArgumentException("AddOperation requires exactly 2 arguments");
    }
    TreeValue a = arguments[0];
    TreeValue b = arguments[1];
    if (a instanceof TreeNumber numA && b instanceof TreeNumber numB) {
      // Add numbers using BigDecimal's add method
      BigDecimal result = numA.getValue().add(numB.getValue());
      return new TreeNumber(result);
    } else if (a instanceof TreeString || b instanceof TreeString) {
      // Concatenate strings
      String result = a.getValue().toString() + b.getValue().toString();
      return new TreeString(result);
    } else {
      throw new IllegalArgumentException("AddOperation only supports numbers or strings");
    }
  }
}
