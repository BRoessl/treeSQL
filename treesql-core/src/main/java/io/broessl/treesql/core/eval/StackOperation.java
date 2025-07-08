package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.eval.stack.Stackable;
import io.broessl.treesql.core.types.TreeValue;

public abstract class StackOperation implements Stackable {

  public abstract String getSymbol();

  public abstract int getArgumentSize();

  public abstract TreeValue call(TreeValue[] arguments);

  public String toString() {
    return getSymbol();
  }
}
