package io.broessl.treesql.spi;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreePrimitive;
import java.util.List;
import java.util.Optional;

public interface NavigableTreeProvider {

  String getDirective();

  Optional<NavigableTreeNode> buildTreeRoot(TreePrimitive fromContent);

  Optional<NavigableTreeNode> attachTreeNode(
      TreePrimitive fromContent, NavigableTreeNode parentNode, List<String> arguments);
}
