package io.broessl.treesql.core.types;

import io.broessl.treesql.core.ScannableTreeNode;

public final class TreeValueAt extends TreeContextualPrimitive {

  String atExpression;

  public TreeValueAt(String atExpression) {
    this.atExpression = atExpression;
  }

  @Override
  public String toString() {
    return atExpression;
  }

  @Override
  public TreePrimitive getPrimitiveValue(ScannableTreeNode stn) {
    return expectAsNavigableTreeNode(stn, atExpression).getValue();
  }
}
