package io.broessl.treesql.core.types;

import io.broessl.treesql.core.ScannableTreeNode;
import java.util.List;

public final class TreeValueAt extends TreeContextValue {

  String atExpression;

  public TreeValueAt(String atExpression) {
    this.atExpression = atExpression;
  }

  @Override
  public String toString() {
    return atExpression;
  }

  @Override
  public TreeValue getPrimitiveValue(ScannableTreeNode stn) {
    return expectAsNavigableTreeNode(stn, atExpression).getValue();
  }

  @Override
  public List<String> getUsedRangedLiterals() {
    return List.of(atExpression.substring(1));
  }
}
