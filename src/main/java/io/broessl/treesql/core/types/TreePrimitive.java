package io.broessl.treesql.core.types;

public sealed abstract class TreePrimitive extends TreeValue implements Comparable<TreePrimitive>
        permits TreeNull, TreeBool, TreeList, TreeNodeIdentifier {

    public abstract Object nativeValue();

    public final static TreeNull NULL = new TreeNull();

}
