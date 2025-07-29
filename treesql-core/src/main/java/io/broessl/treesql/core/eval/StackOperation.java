package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.eval.stack.Stackable;
import io.broessl.treesql.core.types.TreeValue;

public abstract class StackOperation implements Stackable {

  public abstract String getSymbol();

  /**
   * Returns the number of arguments required for this stack operation. Returns -1 if the operation
   * can consume N arguments where N is defined by the values enclosed by parenthesis.
   *
   * @return the number of arguments as an integer or -1 for operations with up to N arguments
   */
  public abstract int getArgumentSize();

  public abstract TreeValue call(TreeValue[] arguments);

  public String toString() {
    return getSymbol();
  }
}
