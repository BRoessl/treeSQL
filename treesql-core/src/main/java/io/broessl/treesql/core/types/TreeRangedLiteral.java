package io.broessl.treesql.core.types;

import io.broessl.treesql.core.ScannableTreeNode;
import java.util.List;

public final class TreeRangedLiteral extends TreeContextValue {

  String literal;

  public TreeRangedLiteral(String literal) {
    this.literal = literal;
  }

  @Override
  public String toString() {
    return this.literal;
  }

  @Override
  public TreeValue getPrimitiveValue(ScannableTreeNode stn) {
    return expectAsStringOrInteger(stn, literal);
  }

  @Override
  public List<String> getUsedRangedLiterals() {
    return List.of(literal);
  }
}
