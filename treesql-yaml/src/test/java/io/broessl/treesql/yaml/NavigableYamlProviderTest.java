package io.broessl.treesql.yaml;

import io.broessl.treesql.core.types.TreeString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NavigableYamlProviderTest {

  String yamlExample =
      """
      name: "John Doe"
      age: 30
      address:
        street: "123 Main St"
        city: "Anytown"
      hobbies:
        - reading
        - swimming
      """;

  String multiDocumentYaml =
      """
      ---
      name: "John Doe"
      age: 30
      ---
      name: "Jane Smith"
      age: 25
      """;

  @Test
  void testAttachTreeNode() {
    var result = new NavigableYamlProvider().buildTreeRoot(new TreeString(yamlExample));

    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals("John Doe", result.get().getChild("name").get().getValue().toString());
    Assertions.assertEquals("30", result.get().getChild("age").get().getValue().toString());
    Assertions.assertEquals(
        "123 Main St",
        result.get().getChild("address").get().getChild("street").get().getValue().toString());
    Assertions.assertEquals(
        "reading",
        result.get().getChild("hobbies").get().getChild("0").get().getValue().toString());
  }
}
