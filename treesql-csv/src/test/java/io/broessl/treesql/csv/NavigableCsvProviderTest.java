package io.broessl.treesql.csv;

import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.json.NavigableJsonNode;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NavigableCsvProviderTest {

  String csvExample =
      """
            "foo","bar"
            "1","2"
            "3","4"
            """;

  @Test
  void testAttachTreeNode() {
    var result =
        new NavigableCsvProvider()
            .attachTreeNode(
                new TreeString(csvExample), NavigableJsonNode.fromContent("null"), List.of());
    Assertions.assertEquals(
        "1", result.get().getChildNode("0").get().getChildNode("foo").get().getValue().toString());
    Assertions.assertEquals(
        "4", result.get().getChildNode("1").get().getChildNode("bar").get().getValue().toString());
  }
}
