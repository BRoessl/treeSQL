package io.broessl.treesql.core.types;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.ScannableTreeNode;

public abstract sealed class TreeContextualPrimitive extends TreeValue
    permits TreeRangedLiteral, TreeValueAt, TreeRangedJSONPointer, TreeFullPath {

  public abstract TreePrimitive getPrimitiveValue(ScannableTreeNode stn);

  static TreeString expectAsString(ScannableTreeNode stn, String context) {
    Object binding = stn.getContext().getBinding(context);
    if (binding == null) {
      throw new IllegalArgumentException(context + " is not a valid identifier");
    }
    return new TreeString((String) binding);
  }

  static TreePrimitive expectAsStringOrInteger(ScannableTreeNode stn, String context) {
    Object binding = stn.getContext().getBinding(context);
    if (binding instanceof TreeNodeIdentifier treePrimitive) {
      return treePrimitive;
    }
    if (binding instanceof String str) {
      return new TreeString(str);
    }
    if (binding instanceof Number number) {
      return new TreeNumber(number);
    }
    throw new IllegalArgumentException(context + " is not a valid identifier");
  }

  static NavigableTreeNode expectAsNavigableTreeNode(ScannableTreeNode stn, String context) {
    Object binding = stn.getContext().getBinding(context);
    if (binding == null) {
      throw new IllegalArgumentException("'" + context + "'' is not a valid identifier");
    }
    return (NavigableTreeNode) binding;
  }
}
