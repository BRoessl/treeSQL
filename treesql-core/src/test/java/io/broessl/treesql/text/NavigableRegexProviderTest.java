package io.broessl.treesql.text;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.json.NavigableJsonNode;
import java.util.List;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NavigableRegexProviderTest {

  private NavigableRegexProvider provider;
  private NavigableTreeNode parentNode;

  @BeforeEach
  void setUp() {
    provider = new NavigableRegexProvider();
    parentNode = NavigableJsonNode.linkRoot(NavigableJsonNode.OM.createObjectNode());
  }

  @Test
  void testBuildTreeRootThrowsException() {
    TreeString content = new TreeString("test string");
    Assertions.assertThrows(
        UnsupportedOperationException.class,
        () -> {
          provider.buildTreeRoot(content);
        });
  }

  @Test
  void testAttachTreeNodeSimpleMatch() {
    TreeString content = new TreeString("Hello World");
    List<String> regex = List.of("Hello");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should return the matched text as a simple text node
    Assertions.assertFalse(node.isListNode());
    Assertions.assertFalse(node.isMapNode());
    Assertions.assertEquals("Hello", node.getValue().nativeValue());
    Assertions.assertEquals(parentNode, node.getParentNode().orElse(null));
  }

  @Test
  void testAttachTreeNodeNoMatch() {
    TreeString content = new TreeString("Hello World");
    List<String> regex = List.of("xyz");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testAttachTreeNodeWithUnnamedGroups() {
    TreeString content = new TreeString("John Doe, age 30");
    List<String> regex = List.of("(\\w+) (\\w+), age (\\d+)");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should return an array node with captured groups
    Assertions.assertTrue(node.isListNode());
    Assertions.assertFalse(node.isMapNode());

    // Should have 3 groups (excluding the whole match)
    Assertions.assertEquals(3, node.getSize().orElse(0));

    // Verify each group
    Optional<NavigableTreeNode> group1 = node.getChildNode("0");
    Assertions.assertTrue(group1.isPresent());
    Assertions.assertEquals("John", group1.get().getValue().nativeValue());

    Optional<NavigableTreeNode> group2 = node.getChildNode("1");
    Assertions.assertTrue(group2.isPresent());
    Assertions.assertEquals("Doe", group2.get().getValue().nativeValue());

    Optional<NavigableTreeNode> group3 = node.getChildNode("2");
    Assertions.assertTrue(group3.isPresent());
    Assertions.assertEquals("30", group3.get().getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeWithNamedGroups() {
    TreeString content = new TreeString("John Doe, age 30");
    List<String> regex = List.of("(?<firstname>\\w+) (?<lastname>\\w+), age (?<age>\\d+)");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should return an object node with named groups
    Assertions.assertFalse(node.isListNode());
    Assertions.assertTrue(node.isMapNode());

    // Should have 3 named groups
    Assertions.assertEquals(3, node.getSize().orElse(0));

    // Verify each named group
    Optional<NavigableTreeNode> firstname = node.getChildNode("firstname");
    Assertions.assertTrue(firstname.isPresent());
    Assertions.assertEquals("John", firstname.get().getValue().nativeValue());

    Optional<NavigableTreeNode> lastname = node.getChildNode("lastname");
    Assertions.assertTrue(lastname.isPresent());
    Assertions.assertEquals("Doe", lastname.get().getValue().nativeValue());

    Optional<NavigableTreeNode> age = node.getChildNode("age");
    Assertions.assertTrue(age.isPresent());
    Assertions.assertEquals("30", age.get().getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeWithNullArgument() {
    TreeString content = new TreeString("test");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, null);

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testAttachTreeNodeWithEmptyArgument() {
    TreeString content = new TreeString("test");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, List.of());

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testAttachTreeNodeWithNonTreeString() {
    TreeNumber content = new TreeNumber(42);
    List<String> regex = List.of("\\d+");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testAttachTreeNodeWithTreeNull() {
    List<String> regex = List.of("test");

    Optional<NavigableTreeNode> result =
        provider.attachTreeNode(TreeNull.INSTANCE, parentNode, regex);

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testAttachTreeNodeWithTreeBool() {
    TreeBool content = new TreeBool(true);
    List<String> regex = List.of("true");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testAttachTreeNodeWithInvalidRegex() {
    TreeString content = new TreeString("test");
    List<String> invalidRegex = List.of("[invalid");

    Assertions.assertThrows(
        PatternSyntaxException.class,
        () -> {
          provider.attachTreeNode(content, parentNode, invalidRegex);
        });
  }

  @Test
  void testAttachTreeNodeWithComplexRegex() {
    TreeString content = new TreeString("Email: john.doe@example.com, Phone: +1-555-123-4567");
    List<String> regex = List.of("Email: ([\\w\\.]+@[\\w\\.]+), Phone: (\\+?[\\d\\-]+)");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should return an array with 2 captured groups
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(2, node.getSize().orElse(0));

    Optional<NavigableTreeNode> email = node.getChildNode("0");
    Assertions.assertTrue(email.isPresent());
    Assertions.assertEquals("john.doe@example.com", email.get().getValue().nativeValue());

    Optional<NavigableTreeNode> phone = node.getChildNode("1");
    Assertions.assertTrue(phone.isPresent());
    Assertions.assertEquals("+1-555-123-4567", phone.get().getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeWithComplexNamedGroups() {
    TreeString content = new TreeString("Email: john.doe@example.com, Phone: +1-555-123-4567");
    List<String> regex =
        List.of("Email: (?<email>[\\w\\.]+@[\\w\\.]+), Phone: (?<phone>\\+?[\\d\\-]+)");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should return an object with named groups
    Assertions.assertTrue(node.isMapNode());
    Assertions.assertEquals(2, node.getSize().orElse(0));

    Optional<NavigableTreeNode> email = node.getChildNode("email");
    Assertions.assertTrue(email.isPresent());
    Assertions.assertEquals("john.doe@example.com", email.get().getValue().nativeValue());

    Optional<NavigableTreeNode> phone = node.getChildNode("phone");
    Assertions.assertTrue(phone.isPresent());
    Assertions.assertEquals("+1-555-123-4567", phone.get().getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeWithPartialMatch() {
    TreeString content = new TreeString("The quick brown fox jumps over the lazy dog");
    List<String> regex = List.of("brown (\\w+)");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should return an array with 1 captured group
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(1, node.getSize().orElse(0));

    Optional<NavigableTreeNode> group = node.getChildNode("0");
    Assertions.assertTrue(group.isPresent());
    Assertions.assertEquals("fox", group.get().getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeWithMultipleMatches() {
    TreeString content = new TreeString("abc123def456ghi789");
    List<String> regex = List.of("(\\d+)");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should return only the first match
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(1, node.getSize().orElse(0));

    Optional<NavigableTreeNode> group = node.getChildNode("0");
    Assertions.assertTrue(group.isPresent());
    Assertions.assertEquals("123", group.get().getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeWithEmptyGroups() {
    TreeString content = new TreeString("test");
    List<String> regex = List.of("t(e?)(s?)(t?)");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should return an array with 3 groups
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(3, node.getSize().orElse(0));

    // First group should be "e"
    Optional<NavigableTreeNode> group1 = node.getChildNode("0");
    Assertions.assertTrue(group1.isPresent());
    Assertions.assertEquals("e", group1.get().getValue().nativeValue());

    // Second group should be "s"
    Optional<NavigableTreeNode> group2 = node.getChildNode("1");
    Assertions.assertTrue(group2.isPresent());
    Assertions.assertEquals("s", group2.get().getValue().nativeValue());

    // Third group should be "t"
    Optional<NavigableTreeNode> group3 = node.getChildNode("2");
    Assertions.assertTrue(group3.isPresent());
    Assertions.assertEquals("t", group3.get().getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeWithNullParent() {
    TreeString content = new TreeString("test");
    List<String> regex = List.of("test");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, null, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should work with null parent
    Assertions.assertTrue(node.getParentNode().isEmpty());
    Assertions.assertEquals("test", node.getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeWithCaseInsensitiveRegex() {
    TreeString content = new TreeString("Hello WORLD");
    List<String> regex = List.of("(?i)hello (world)");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should match case-insensitively
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(1, node.getSize().orElse(0));

    Optional<NavigableTreeNode> group = node.getChildNode("0");
    Assertions.assertTrue(group.isPresent());
    Assertions.assertEquals("WORLD", group.get().getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeWithSpecialCharacters() {
    TreeString content = new TreeString("Price: $19.99, Tax: 8.5%");
    List<String> regex = List.of("Price: \\$([\\d\\.]+), Tax: ([\\d\\.]+)%");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should handle special characters
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(2, node.getSize().orElse(0));

    Optional<NavigableTreeNode> price = node.getChildNode("0");
    Assertions.assertTrue(price.isPresent());
    Assertions.assertEquals("19.99", price.get().getValue().nativeValue());

    Optional<NavigableTreeNode> tax = node.getChildNode("1");
    Assertions.assertTrue(tax.isPresent());
    Assertions.assertEquals("8.5", tax.get().getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeWithUnicodeCharacters() {
    TreeString content = new TreeString("Name: José García, Age: 25");
    // Use a more inclusive pattern that works with Unicode characters
    List<String> regex = List.of("Name: ([^,]+), Age: (\\d+)");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Should handle Unicode characters
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(2, node.getSize().orElse(0));

    Optional<NavigableTreeNode> name = node.getChildNode("0");
    Assertions.assertTrue(name.isPresent());
    Assertions.assertEquals("José García", name.get().getValue().nativeValue());

    Optional<NavigableTreeNode> age = node.getChildNode("1");
    Assertions.assertTrue(age.isPresent());
    Assertions.assertEquals("25", age.get().getValue().nativeValue());
  }

  @Test
  void testAttachTreeNodeNavigationBehavior() {
    TreeString content = new TreeString("First Second Third");
    List<String> regex = List.of("(\\w+) (\\w+) (\\w+)");

    Optional<NavigableTreeNode> result = provider.attachTreeNode(content, parentNode, regex);

    Assertions.assertTrue(result.isPresent());
    NavigableTreeNode node = result.get();

    // Test navigation through the array
    Assertions.assertTrue(node.isListNode());
    Assertions.assertEquals(3, node.getSize().orElse(0));

    // Test child navigation
    Optional<NavigableTreeNode> child1 = node.getChildNode("0");
    Optional<NavigableTreeNode> child2 = node.getChildNode("1");
    Optional<NavigableTreeNode> child3 = node.getChildNode("2");

    Assertions.assertTrue(child1.isPresent());
    Assertions.assertTrue(child2.isPresent());
    Assertions.assertTrue(child3.isPresent());

    // Test parent navigation
    Assertions.assertEquals(node, child1.get().getParentNode().orElse(null));
    Assertions.assertEquals(node, child2.get().getParentNode().orElse(null));
    Assertions.assertEquals(node, child3.get().getParentNode().orElse(null));

    // Test sibling navigation
    Optional<NavigableTreeNode> nextSibling = child1.get().getSibling(1);
    Assertions.assertTrue(nextSibling.isPresent());
    Assertions.assertEquals("Second", nextSibling.get().getValue().nativeValue());

    Optional<NavigableTreeNode> prevSibling = child2.get().getSibling(-1);
    Assertions.assertTrue(prevSibling.isPresent());
    Assertions.assertEquals("First", prevSibling.get().getValue().nativeValue());
  }
}
