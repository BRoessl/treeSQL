package io.broessl.treesql.core.types;

public abstract sealed class TreePrimitive extends TreeValue implements Comparable<TreePrimitive>
        permits TreeNull, TreeBool, TreeList, TreeNodeIdentifier {

    public abstract Object nativeValue();

}
