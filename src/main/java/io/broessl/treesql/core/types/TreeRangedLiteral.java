package io.broessl.treesql.core.types;

import io.broessl.treesql.core.ScannableTreeNode;

public final class TreeRangedLiteral extends TreeContextualPrimitive {

    String literal;

    public TreeRangedLiteral(String literal) {
        this.literal = literal;
    }

    @Override
    public String toString() {
        return this.literal;
    }

    @Override
    public TreePrimitive getPrimitiveValue(ScannableTreeNode stn) {
        return expectAsStringOrInteger(stn, literal);
    }

}
