package io.broessl.treesql.core.types;

public abstract sealed class TreeNodeIdentifier extends TreePrimitive
    permits TreeString, TreeNumber {}
