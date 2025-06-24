package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.eval.stack.Stackable;
import io.broessl.treesql.core.types.TreePrimitive;

public abstract class StackOperation implements Stackable {

  public abstract String getSymbol();

  public abstract int getArgumentSize();

  public abstract TreePrimitive call(TreePrimitive[] arguments);

  public String toString() {
    return getSymbol();
  }
}
