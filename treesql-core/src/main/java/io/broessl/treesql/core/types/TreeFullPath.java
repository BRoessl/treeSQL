package io.broessl.treesql.core.types;

import io.broessl.treesql.core.ScannableTreeNode;

public final class TreeFullPath extends TreeContextValue {

  String pathLiteral;

  TreeFullPath(String pathLiteral) {
    this.pathLiteral = pathLiteral;
  }

  @Override
  public String toString() {
    return this.pathLiteral;
  }

  @Override
  public TreeValue getPrimitiveValue(ScannableTreeNode stn) {
    return expectAsString(stn, pathLiteral);
  }
}
