package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;

public class IsOperation extends StackOperation {

  @Override
  public String getSymbol() {
    return "IS";
  }

  @Override
  public int getArgumentSize() {
    return 2;
  }

  @Override
  public TreeValue call(TreeValue[] arguments) {
    if (arguments == null || arguments.length != 2) {
      throw new IllegalArgumentException("IsOperation requires exactly 2 arguments");
    }
    TreeValue value = arguments[0];
    TreeValue type = arguments[1];
    if (type instanceof TreeString typeDefinition) {
      if (typeDefinition.getValue().equals("STRING")) {
        return new TreeBool(value instanceof TreeString);
      }
      if (typeDefinition.getValue().equals("NUMBER")) {
        return new TreeBool(value instanceof TreeNumber);
      }
      if (typeDefinition.getValue().equals("BOOL")) {
        return new TreeBool(value instanceof TreeBool);
      }
      if (typeDefinition.getValue().equals("NULL")) {
        return new TreeBool(value instanceof TreeNull);
      }
      if (typeDefinition.getValue().equals("LIST")) {
        return new TreeBool(value instanceof TreeList);
      }
    }
    throw new IllegalArgumentException(
        "IsOperation second argument must be a String 'STRING', 'NUMBER', 'BOOL', 'NULL' or 'LIST'");
  }
}
