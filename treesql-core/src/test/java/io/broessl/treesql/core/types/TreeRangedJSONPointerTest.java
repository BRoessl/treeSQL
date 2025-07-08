package io.broessl.treesql.core.types;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.testutils.TestWithJsonData;
import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.ScannableTreeNode;
import io.broessl.treesql.core.TransientScanContext;
import io.broessl.treesql.json.NavigableJsonNode;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TreeRangedJSONPointerTest extends TestWithJsonData {

  private ScannableTreeNode rootNode;

  @BeforeEach
  void setUp() {
    NavigableTreeNode root = NavigableJsonNode.linkRoot(testDataSimpleDataTree());
    rootNode = ScannableTreeNode.forRoot(root);
  }

  @Test
  void testConstructor() {
    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("/foo");
    assertEquals("/foo", pointer.toString());
  }

  @Test
  void testConstructorWithContextAwareSelection() {
    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("~/contextPath");
    assertEquals("~/contextPath", pointer.toString());
  }

  @Test
  void testGetPrimitiveValueReturnsNull() {
    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("/nonexistent");
    Assertions.assertInstanceOf(TreeNull.class, pointer.getPrimitiveValue(rootNode));
  }

  @Test
  void testGetPrimitiveValueReturnsSingleString() {
    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("/highly/nested/objects");
    TreeValue result = pointer.getPrimitiveValue(rootNode);

    TreeValue assertInstanceOf = assertInstanceOf(TreeBool.class, result);
    assertEquals(true, ((TreeBool) assertInstanceOf).getValue());
  }

  @Test
  void testGetPrimitiveValueWithStringValue() {
    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("/foo/0");
    TreeValue result = pointer.getPrimitiveValue(rootNode);

    var str = assertInstanceOf(TreeString.class, result);
    assertEquals("bar", (str).getValue());
  }

  @Test
  void testGetPrimitiveValueWithArrayAsWhole() {
    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("/foo");
    TreeValue result = pointer.getPrimitiveValue(rootNode);

    assertEquals("[0, 1]", result.toString());
  }

  @Test
  void testGetPrimitiveValueWithNestedObject() {
    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("/highly");
    TreeList result = (TreeList) pointer.getPrimitiveValue(rootNode);

    List<Object> resultStr = result.getValue();
    assertEquals(
        "[nested]", resultStr.toString(), "Result should contain 'nested', got: " + resultStr);
  }

  @Test
  void testGetPrimitiveValueWithDifferentDataTypes() {
    // Create test data with different types
    String jsonWithTypes =
        """
        {
          "string": "hello",
          "number": 42,
          "boolean": true,
          "null": null
        }
        """;

    try {
      NavigableTreeNode typeRoot =
          NavigableJsonNode.linkRoot(OM.valueToTree(OM.readValue(jsonWithTypes, Object.class)));
      ScannableTreeNode typeNode = ScannableTreeNode.forRoot(typeRoot);

      // Test string value
      TreeRangedJSONPointer stringPointer = new TreeRangedJSONPointer("/string");
      var stringResult = (TreeString) stringPointer.getPrimitiveValue(typeNode);
      assertInstanceOf(TreeString.class, stringResult);
      assertEquals("hello", ((TreeString) stringResult).getValue());

      // Test number value
      TreeRangedJSONPointer numberPointer = new TreeRangedJSONPointer("/number");
      var numberResult = (TreeNumber) numberPointer.getPrimitiveValue(typeNode);
      assertInstanceOf(TreeNumber.class, numberResult);
      assertEquals(42, ((TreeNumber) numberResult).getValue().intValue());

      // Test boolean value - note that it's now in correct JSON format (lowercase)
      TreeRangedJSONPointer boolPointer = new TreeRangedJSONPointer("/boolean");
      TreeBool boolResult = (TreeBool) boolPointer.getPrimitiveValue(typeNode);
      assertInstanceOf(TreeBool.class, boolResult);
      assertEquals(true, ((TreeBool) boolResult).getValue());

      // Test null value - note that it's now in correct JSON format (lowercase)
      TreeRangedJSONPointer nullPointer = new TreeRangedJSONPointer("/null");
      var nullResult = (TreeNull) nullPointer.getPrimitiveValue(typeNode);
      assertInstanceOf(TreeNull.class, nullResult);
      assertEquals(null, ((TreeNull) nullResult).getValue());
    } catch (Exception e) {
      fail("Failed to parse test JSON: " + e.getMessage());
    }
  }

  @Test
  void testContextAwarePointerWithNonContextAware() {
    // Test that non-context-aware pointers return themselves
    TreeRangedJSONPointer normalPointer = new TreeRangedJSONPointer("/foo");
    TreeRangedJSONPointer resolved = normalPointer.contextAware(rootNode);

    // Should return the same instance since it's not context-aware
    assertSame(normalPointer, resolved);
    assertEquals("/foo", resolved.toString());
  }

  @Test
  void testIllegalStateExceptionNotThrown() {
    // This test verifies that the IllegalStateException case is unreachable
    // under normal circumstances due to the logic structure

    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("/foo");
    assertDoesNotThrow(() -> pointer.getPrimitiveValue(rootNode));

    TreeRangedJSONPointer nonExistentPointer = new TreeRangedJSONPointer("/nonexistent");
    assertDoesNotThrow(() -> nonExistentPointer.getPrimitiveValue(rootNode));
  }

  @Test
  void testContextAwareWithMissingContext() {
    // Test what happens when context binding is missing
    TransientScanContext emptyContext = new TransientScanContext();
    ScannableTreeNode nodeWithEmptyContext =
        new ScannableTreeNode(NavigableJsonNode.linkRoot(testDataSimpleDataTree()), emptyContext);

    TreeRangedJSONPointer contextPointer = new TreeRangedJSONPointer("~/missingContext");

    // This should throw an exception when trying to resolve the context
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          contextPointer.contextAware(nodeWithEmptyContext);
        });
  }

  @Test
  void testToString() {
    TreeRangedJSONPointer simplePointer = new TreeRangedJSONPointer("/simple/path");
    assertEquals("/simple/path", simplePointer.toString());

    TreeRangedJSONPointer contextPointer = new TreeRangedJSONPointer("~/contextVar/path");
    assertEquals("~/contextVar/path", contextPointer.toString());
  }

  @Test
  void testGetPrimitiveValueWithNonContextAwarePathInContextNode() {
    // Test that even when using a node with context, regular paths work
    TransientScanContext context = new TransientScanContext();
    ScannableTreeNode nodeWithContext =
        new ScannableTreeNode(NavigableJsonNode.linkRoot(testDataSimpleDataTree()), context);

    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("/highly/nested/objects");
    TreeValue result = pointer.getPrimitiveValue(nodeWithContext);

    assertInstanceOf(TreeBool.class, result);
    assertEquals(true, ((TreeBool) result).getValue());
  }

  @Test
  void testScanReturnsSingleResult() {
    // Test the case where scan returns exactly one result
    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("/highly/nested/objects");
    TreeValue result = pointer.getPrimitiveValue(rootNode);

    assertInstanceOf(TreeBool.class, result);
    assertNotNull(result);
  }

  @Test
  void testScanReturnsEmptyResult() {
    // Test the case where scan returns empty list
    TreeRangedJSONPointer pointer = new TreeRangedJSONPointer("/definitely/does/not/exist");
    TreeValue result = pointer.getPrimitiveValue(rootNode);

    assertTrue(result.getValue() == null);
  }
}
