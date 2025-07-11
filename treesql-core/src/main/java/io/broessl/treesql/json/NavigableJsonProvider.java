package io.broessl.treesql.json;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import io.broessl.treesql.spi.NavigableTreeProvider;
import java.util.List;
import java.util.Optional;

public class NavigableJsonProvider implements NavigableTreeProvider {

  @Override
  public String getDirective() {
    return "JSON";
  }

  @Override
  public Optional<NavigableTreeNode> buildTreeRoot(TreeValue fromContent) {
    if (fromContent instanceof TreeString tString) {
      return Optional.ofNullable(NavigableJsonNode.fromContent(tString.getValue()));
    }
    return Optional.empty();
  }

  @Override
  public Optional<NavigableTreeNode> attachTreeNode(
      String rootName, TreeValue fromContent, NavigableTreeNode parentNode, List<String> argument) {
    if (fromContent instanceof TreeString tString) {
      return Optional.ofNullable(
          NavigableJsonNode.fromContent(tString.getValue(), parentNode, rootName));
    }
    return Optional.empty();
  }
}
