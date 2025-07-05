package io.broessl.treesql.json;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.spi.NavigableTreeProvider;
import java.util.List;
import java.util.Optional;

public class NavigableJsonProvider implements NavigableTreeProvider {

  @Override
  public String getDirective() {
    return "~AS_JSON";
  }

  @Override
  public Optional<NavigableTreeNode> buildTreeRoot(TreePrimitive fromContent) {
    if (fromContent instanceof TreeString tString) {
      return Optional.ofNullable(NavigableJsonNode.fromContent(tString.nativeValue()));
    }
    return Optional.empty();
  }

  @Override
  public Optional<NavigableTreeNode> attachTreeNode(
      TreePrimitive fromContent, NavigableTreeNode parentNode, List<String> argument) {
    if (fromContent instanceof TreeString tString) {
      return Optional.ofNullable(
          NavigableJsonNode.fromContent(tString.nativeValue(), parentNode, "!!JSON"));
    }
    return Optional.empty();
  }
}
