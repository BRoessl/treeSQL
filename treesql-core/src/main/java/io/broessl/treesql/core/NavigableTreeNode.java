package io.broessl.treesql.core;

import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The NavigableTreeNode represents a single node in the tree and is used to navigate back (to
 * parent), forth (to children) or sideways (to siblings) the tree hierarchy. This is the bare
 * minimum to support tree traversal based on ranged JSONPointers.
 */
public interface NavigableTreeNode {

  /**
   * This method must be implemented for all named or indexed nodes. Only the root node might and
   * should return null;
   *
   * @return The name as {@link TreeString} or - in case of nodes inside an indexable list - the
   *     index as {@link TreeNumber}.
   */
  TreeNodeIdentifier getSelfName();

  /**
   * @return The parent node. Only the root node should return {@link Optional#empty()}.
   */
  Optional<NavigableTreeNode> getParentNode();

  /**
   * @param nameOrIndex The name of the requested child node. Might be interpreted as integer
   *     ([0-9]*) in case of list nodes.
   * @return The {@link NavigableTreeNode} matching the name or index.
   */
  Optional<NavigableTreeNode> getChildNode(String nameOrIndex);

  /**
   * This method is only applicable on nodes placed inside a list.
   *
   * @param indexOffset The index offset to the this node. Thus '-1' should return the previous
   *     sibling, '0' should return itself.
   * @return {@link Optional#empty()} if they is no sibling, otherwise the sibling at the position
   *     offset.
   */
  Optional<NavigableTreeNode> getSibling(Integer indexOffset);

  /**
   * @return A stream of all known children.
   */
  Stream<NavigableTreeNode> children();

  /**
   * The amount of known children.
   *
   * @return Leaf value nodes should return {@link Optional#empty()}, empty map nodes or list nodes
   *     should return 0.
   */
  Optional<Integer> getSize();

  /**
   * Builds a valid JSONPointer as String, starting from root. '~' and '/' must be escaped
   *
   * @return A String representing the JSONPointer up to and including this node.
   */
  default String absolutePath() {
    if (getParentNode().isEmpty()) {
      return "";
    }
    return getParentNode().orElseThrow().absolutePath()
        + "/"
        + getSelfName().nativeValue().toString().replaceAll("~", "~0").replaceAll("/", "~1");
  }

  /**
   * The default implementation checks for empty {@link #getParentNode()} to figure out if itself is
   * the root. Otherwise the request is delegated to {@link #getParentNode()}.
   *
   * @return The root node.
   */
  default NavigableTreeNode getRoot() {
    if (getParentNode().isEmpty()) {
      return this;
    }
    return getParentNode().orElseThrow().getRoot();
  }

  /**
   * @return true, if it might contain only indexed children
   */
  boolean isListNode();

  /**
   * @return true, if it might contain named children.
   */
  boolean isMapNode();

  /**
   * This method returns the value this node is pointing at. Every node must return a valid {@link
   * TreePrimitive} but could return {@link TreeNull}. A map node should return a {@link TreeList}
   * containing children names. A list node should return a {@link TreeList} with all indices.
   *
   * @return a TreePrimitive, never null but possible TreeNull
   */
  TreePrimitive getValue();
}
