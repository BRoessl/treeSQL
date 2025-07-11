package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import java.math.BigDecimal;

public class AsNumberFunction extends StackOperation {
  @Override
  public String getSymbol() {
    return "AS_NUMBER";
  }

  @Override
  public int getArgumentSize() {
    return 1;
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    try {
      TreeValue convertMe = arguments[0];
      if (convertMe instanceof TreeNumber number) {
        return number;
      } else if (convertMe instanceof TreeString string) {
        return TreeValue.parseNumber(string.getValue());
      } else if (convertMe instanceof TreeBool bool) {
        return new TreeNumber(bool.getValue() ? BigDecimal.ONE : BigDecimal.ZERO);
      }
    } catch (Exception e) {
      // ignore conversion errors
    }
    // NULL is fallback
    return TreeNull.INSTANCE;
  }
}
