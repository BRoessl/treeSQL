package io.broessl.treesql.text;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import io.broessl.treesql.json.NavigableJsonNode;
import io.broessl.treesql.spi.NavigableTreeProvider;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class NavigableRegexProvider implements NavigableTreeProvider {

  @Override
  public String getDirective() {
    return "REGEX";
  }

  @Override
  public Optional<NavigableTreeNode> buildTreeRoot(TreeValue fromContent) {
    throw new UnsupportedOperationException(
        "NavigableRegexProvider does only support building attached tree nodes.");
  }

  @Override
  public Optional<NavigableTreeNode> attachTreeNode(
      String rootName, TreeValue fromContent, NavigableTreeNode parentNode, List<String> argument) {

    if (argument != null && !argument.isEmpty() && fromContent instanceof TreeString tString) {
      Pattern pattern = Pattern.compile(argument.get(0));
      var matcher = pattern.matcher(tString.getValue());
      if (!matcher.find()) {
        return Optional.empty();
      }
      if (matcher.groupCount() == 0) {
        // no groups specified, just the matching string gets returned (working as
        // filter)
        var matchText = NavigableJsonNode.OM.getNodeFactory().textNode(matcher.group(0));
        return Optional.of(new NavigableJsonNode(matchText, parentNode, rootName));
      }
      var namedGroups = pattern.namedGroups();
      if (namedGroups.isEmpty()) {
        // groups are unnamed, handle as array
        var array = NavigableJsonNode.OM.createArrayNode();
        int groups = matcher.groupCount();
        // do not include the whole match
        for (int i = 1; i <= groups; i++) {
          array.add(matcher.group(i));
        }
        return Optional.of(new NavigableJsonNode(array, parentNode, rootName));
      } else {
        // groups are named, handle as object
        var object = NavigableJsonNode.OM.createObjectNode();
        namedGroups.keySet().forEach(key -> object.put(key, matcher.group(key)));
        return Optional.of(new NavigableJsonNode(object, parentNode, rootName));
      }
    }
    return Optional.empty();
  }
}
