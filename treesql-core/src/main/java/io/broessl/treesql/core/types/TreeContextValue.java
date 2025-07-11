package io.broessl.treesql.core.types;

import io.broessl.treesql.core.ScannableTreeNode;
import java.util.List;

public abstract sealed class TreeContextValue extends TreeStackableValue
    permits TreeRangedLiteral, TreeValueAt, TreeRangedJSONPointer, TreeFullPath {

  public abstract TreeValue getPrimitiveValue(ScannableTreeNode stn);

  static TreeString expectAsString(ScannableTreeNode stn, String context) {
    Object binding = stn.getContext().getObject(context);
    if (binding == null) {
      throw new IllegalArgumentException(context + " is not a valid identifier");
    }
    return new TreeString((String) binding);
  }

  static TreeNodeIdentifier expectAsStringOrInteger(ScannableTreeNode stn, String context) {
    Object binding = stn.getContext().getObject(context);
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

  static TreeValue expectTreeValue(ScannableTreeNode stn, String context) {
    Object binding = stn.getContext().getObject(context);
    if (binding == null) {
      throw new IllegalArgumentException("'" + context + "'' is not a valid identifier");
    }
    return (TreeValue) binding;
  }

  public abstract List<String> getUsedRangedLiterals();
}
