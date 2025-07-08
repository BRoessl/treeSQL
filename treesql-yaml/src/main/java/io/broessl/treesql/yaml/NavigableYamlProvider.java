package io.broessl.treesql.yaml;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.json.NavigableJsonNode;
import io.broessl.treesql.spi.NavigableTreeProvider;
import java.util.List;
import java.util.Optional;

public class NavigableYamlProvider implements NavigableTreeProvider {

  private static final YAMLMapper YAML_MAPPER = new YAMLMapper(new YAMLFactory());

  @Override
  public String getDirective() {
    return "~AS_YAML";
  }

  @Override
  public Optional<NavigableTreeNode> buildTreeRoot(TreePrimitive fromContent) {
    if (fromContent instanceof TreeString tString) {
      try {
        return Optional.of(
            new NavigableJsonNode(YAML_MAPPER.readTree(tString.getValue()), null, null));
      } catch (Exception e) {
        // log and ignore
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<NavigableTreeNode> attachTreeNode(
      TreePrimitive fromContent, NavigableTreeNode parentNode, List<String> argument) {
    if (fromContent instanceof TreeString tString) {
      try {
        return Optional.of(
            new NavigableJsonNode(YAML_MAPPER.readTree(tString.getValue()), parentNode, "!!YAML"));
      } catch (Exception e) {
        // log and ignore
      }
    }
    return Optional.empty();
  }
}
