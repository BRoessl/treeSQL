package io.broessl.treesql.spi;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeValue;
import java.util.List;
import java.util.Optional;

public interface NavigableTreeProvider {

  String getDirective();

  Optional<NavigableTreeNode> buildTreeRoot(TreeValue fromContent);

  Optional<NavigableTreeNode> attachTreeNode(
      TreeValue fromContent, NavigableTreeNode parentNode, List<String> arguments);
}
