package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import java.math.BigDecimal;

public class SinOperation extends StackOperation {

  @Override
  public String getSymbol() {
    return "SIN";
  }

  @Override
  public int getArgumentSize() {
    return 1;
  }

  @Override
  public TreePrimitive call(TreePrimitive[] arguments) {
    if (arguments == null || arguments.length != 1) {
      throw new IllegalArgumentException("SIN Operation requires exactly 1 argument");
    }
    TreePrimitive arg = arguments[0];
    if (!(arg instanceof TreeNumber)) {
      throw new IllegalArgumentException("SinOperation requires a numeric argument");
    }
    BigDecimal value = ((TreeNumber) arg).nativeValue();
    return new TreeNumber(Math.sin(value.doubleValue()));
  }
}
