package io.broessl.treesql.text;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.json.NavigableJsonNode;
import io.broessl.treesql.spi.NavigableTreeProvider;
import java.util.List;
import java.util.Optional;

public class NavigableLinesProvider implements NavigableTreeProvider {
  @Override
  public String getDirective() {
    return "~LINES";
  }

  @Override
  public Optional<NavigableTreeNode> buildTreeRoot(TreePrimitive fromContent) {
    if (fromContent instanceof TreeString tString) {
      var array = NavigableJsonNode.OM.createArrayNode();
      tString.getValue().lines().forEach(array::add);
      return Optional.of(
          new NavigableJsonNode(array, null, "!!LINES")); // Create a new instance for the root
    }
    return Optional.empty();
  }

  @Override
  public Optional<NavigableTreeNode> attachTreeNode(
      TreePrimitive fromContent, NavigableTreeNode parentNode, List<String> argument) {
    if (fromContent instanceof TreeString tString) {
      var array = NavigableJsonNode.OM.createArrayNode();
      tString.getValue().lines().forEach(array::add);
      return Optional.of(
          new NavigableJsonNode(
              array, parentNode, "!!LINES")); // Create a new instance for the root
    }
    return Optional.empty();
  }
}
