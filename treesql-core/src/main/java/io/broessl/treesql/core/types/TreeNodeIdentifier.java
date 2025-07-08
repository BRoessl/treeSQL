package io.broessl.treesql.core.types;

public abstract sealed class TreeNodeIdentifier extends TreeValue permits TreeString, TreeNumber {}
