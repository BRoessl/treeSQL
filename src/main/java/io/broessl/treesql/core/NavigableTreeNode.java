package io.broessl.treesql.core;

import java.util.Optional;
import java.util.stream.Stream;

import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.core.types.TreePrimitive;

public interface NavigableTreeNode {

	TreeNodeIdentifier getNameOrIndex();

	Optional<NavigableTreeNode> getPreviousNode();

	Optional<NavigableTreeNode> getNextNode(String nameOrIndex);

	Optional<NavigableTreeNode> getSibling(Integer indexOffset);

	Stream<NavigableTreeNode> getAllNextNodes();

	Optional<Integer> getSelfIndex();

	Optional<Integer> getSize();

	default String absolutePath() {
		if (getPreviousNode().isEmpty()) {
			return "";
		}
		return getPreviousNode().orElseThrow().absolutePath() + "/" + getNameOrIndex().nativeValue().toString();
	}

	default NavigableTreeNode getRoot(){
		if (getPreviousNode().isEmpty()) {
			return this;
		}
		return getPreviousNode().orElseThrow().getRoot();
	}

	boolean isArray();

	boolean isObject();

	TreePrimitive getValue();

}
