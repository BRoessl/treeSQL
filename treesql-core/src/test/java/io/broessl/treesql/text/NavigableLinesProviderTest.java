package io.broessl.treesql.text;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.json.NavigableJsonNode;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NavigableLinesProviderTest {

  private NavigableLinesProvider provider;

  @BeforeEach
  void setUp() {
    provider = new NavigableLinesProvider();
  }

  @Test
  void testBuildTreeRootWithTreeString() {
    // Test with single line
    TreeString singleLine = new TreeString("Hello World");
    Optional<NavigableTreeNode> result = provider.buildTreeRoot(singleLine);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();
    Assertions.assertInstanceOf(NavigableJsonNode.class, node);

    // Verify it's a list node
    Assertions.assertTrue(node.isListNode());
    Assertions.assertFalse(node.isMapNode());

    // Verify the size
    Assertions.assertEquals(1, node.getSize().orElse(0));

    // Verify the content
    TreePrimitive value = node.getValue();
    Assertions.assertInstanceOf(TreeList.class, value);
    TreeList list = (TreeList) value;
    Assertions.assertEquals(1, list.size());

    // Get the first line and verify it
    Optional<NavigableTreeNode> firstLine = node.getChildNode("0");
    Assertions.assertTrue(firstLine.isPresent());
    Assertions.assertEquals("Hello World", firstLine.get().getValue().nativeValue());
  }

  @Test
  void testBuildTreeRootWithMultilineString() {
    TreeString multiLine = new TreeString("Line 1\nLine 2\nLine 3");
    Optional<NavigableTreeNode> result = provider.buildTreeRoot(multiLine);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Verify it's a list node with correct size
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(3, node.getSize().orElse(0));

    // Verify each line
    Optional<NavigableTreeNode> line1 = node.getChildNode("0");
    Assertions.assertTrue(line1.isPresent());
    Assertions.assertEquals("Line 1", line1.get().getValue().nativeValue());

    Optional<NavigableTreeNode> line2 = node.getChildNode("1");
    Assertions.assertTrue(line2.isPresent());
    Assertions.assertEquals("Line 2", line2.get().getValue().nativeValue());

    Optional<NavigableTreeNode> line3 = node.getChildNode("2");
    Assertions.assertTrue(line3.isPresent());
    Assertions.assertEquals("Line 3", line3.get().getValue().nativeValue());
  }

  @Test
  void testBuildTreeRootWithEmptyString() {
    TreeString emptyString = new TreeString("");
    Optional<NavigableTreeNode> result = provider.buildTreeRoot(emptyString);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Empty string returns 0 lines according to String.lines()
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(0, node.getSize().orElse(0));

    // No lines should be present
    Optional<NavigableTreeNode> firstLine = node.getChildNode("0");
    Assertions.assertTrue(firstLine.isEmpty());
  }

  @Test
  void testBuildTreeRootWithStringContainingOnlyNewlines() {
    TreeString newlinesOnly = new TreeString("\n\n\n");
    Optional<NavigableTreeNode> result = provider.buildTreeRoot(newlinesOnly);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should create 3 empty lines (String.lines() behavior)
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(3, node.getSize().orElse(0));

    // All lines should be empty
    for (int i = 0; i < 3; i++) {
      Optional<NavigableTreeNode> line = node.getChildNode(String.valueOf(i));
      Assertions.assertTrue(line.isPresent());
      Assertions.assertEquals("", line.get().getValue().nativeValue());
    }
  }

  @Test
  void testBuildTreeRootWithMixedLineEndingsAndContent() {
    TreeString mixedContent = new TreeString("First line\n\nThird line\n\nFifth line");
    Optional<NavigableTreeNode> result = provider.buildTreeRoot(mixedContent);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    Assertions.assertEquals(5, node.getSize().orElse(0));

    // Verify specific lines
    Assertions.assertEquals("First line", node.getChildNode("0").get().getValue().nativeValue());
    Assertions.assertEquals("", node.getChildNode("1").get().getValue().nativeValue());
    Assertions.assertEquals("Third line", node.getChildNode("2").get().getValue().nativeValue());
    Assertions.assertEquals("", node.getChildNode("3").get().getValue().nativeValue());
    Assertions.assertEquals("Fifth line", node.getChildNode("4").get().getValue().nativeValue());
  }
}
