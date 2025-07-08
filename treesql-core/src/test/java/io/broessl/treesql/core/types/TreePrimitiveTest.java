package io.broessl.treesql.core.types;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TreePrimitiveTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

  @Nested
  class CreateTreePrimitiveTests {

    @Test
    void shouldReturnSamePrimitiveWhenAlreadyTreePrimitive() {
      TreeString original = new TreeString("test");
      TreePrimitive result = TreePrimitive.createTreePrimitive(original);
      assertSame(original, result);
    }

    @Test
    void shouldCreateTreeNullForNull() {
      TreePrimitive result = TreePrimitive.createTreePrimitive(null);
      assertSame(TreeNull.INSTANCE, result);
    }

    @Test
    void shouldCreateTreeBoolForBoolean() {
      TreePrimitive result = TreePrimitive.createTreePrimitive(true);
      assertInstanceOf(TreeBool.class, result);
      assertEquals(true, result.getValue());

      result = TreePrimitive.createTreePrimitive(false);
      assertInstanceOf(TreeBool.class, result);
      assertEquals(false, result.getValue());
    }

    @Test
    void shouldCreateTreeStringForString() {
      TreePrimitive result = TreePrimitive.createTreePrimitive("hello");
      assertInstanceOf(TreeString.class, result);
      assertEquals("hello", result.getValue());
    }

    @Test
    void shouldCreateTreeNumberForNumbers() {
      TreePrimitive result = TreePrimitive.createTreePrimitive(42);
      assertInstanceOf(TreeNumber.class, result);
      assertEquals(42, ((TreeNumber) result).getValue().intValue());

      result = TreePrimitive.createTreePrimitive(3.14);
      assertInstanceOf(TreeNumber.class, result);
      assertEquals(3.14, ((TreeNumber) result).getValue().doubleValue(), 0.001);
    }

    @Test
    void shouldCreateTreeListForList() {
      List<Object> input = Arrays.asList("hello", 42, true);
      TreePrimitive result = TreePrimitive.createTreePrimitive(input);

      assertInstanceOf(TreeList.class, result);
      TreeList list = (TreeList) result;
      assertEquals(3, list.size());

      assertInstanceOf(TreeString.class, list.get(0));
      assertEquals("hello", list.get(0).getValue());

      assertInstanceOf(TreeNumber.class, list.get(1));
      assertEquals(42, ((TreeNumber) list.get(1)).getValue().intValue());

      assertInstanceOf(TreeBool.class, list.get(2));
      assertEquals(true, list.get(2).getValue());
    }

    @Test
    void shouldHandleJsonNodeNull() {
      JsonNode nullNode = nodeFactory.nullNode();
      TreePrimitive result = TreePrimitive.createTreePrimitive(nullNode);
      assertSame(TreeNull.INSTANCE, result);
    }

    @Test
    void shouldHandleJsonNodeMissing() {
      JsonNode missingNode = nodeFactory.missingNode();
      TreePrimitive result = TreePrimitive.createTreePrimitive(missingNode);
      assertSame(TreeNull.INSTANCE, result);
    }

    @Test
    void shouldHandleJsonNodeText() {
      JsonNode textNode = nodeFactory.textNode("test");
      TreePrimitive result = TreePrimitive.createTreePrimitive(textNode);
      assertInstanceOf(TreeString.class, result);
      assertEquals("test", result.getValue());
    }

    @Test
    void shouldHandleJsonNodeBoolean() {
      JsonNode boolNode = nodeFactory.booleanNode(true);
      TreePrimitive result = TreePrimitive.createTreePrimitive(boolNode);
      assertInstanceOf(TreeBool.class, result);
      assertEquals(true, result.getValue());
    }

    @Test
    void shouldHandleJsonNodeNumber() {
      JsonNode numberNode = nodeFactory.numberNode(42);
      TreePrimitive result = TreePrimitive.createTreePrimitive(numberNode);
      assertInstanceOf(TreeNumber.class, result);
      assertEquals(42, ((TreeNumber) result).getValue().intValue());
    }

    @Test
    void shouldHandleJsonNodeArray() throws Exception {
      String jsonArray = "[\"hello\", 42, true]";
      ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(jsonArray);

      TreePrimitive result = TreePrimitive.createTreePrimitive(arrayNode);
      assertInstanceOf(TreeList.class, result);

      TreeList list = (TreeList) result;
      assertEquals(3, list.size());
      assertEquals("hello", list.get(0).getValue());
      assertEquals(42, ((TreeNumber) list.get(1)).getValue().intValue());
      assertEquals(true, list.get(2).getValue());
    }

    @Test
    void shouldThrowForUnsupportedType() {
      Object unsupported = new Object();
      assertThrows(
          IllegalArgumentException.class, () -> TreePrimitive.createTreePrimitive(unsupported));
    }
  }

  @Nested
  class ConvertTests {

    @Test
    void shouldConvertToTreeNull() {
      TreeNull result = TreePrimitive.convert(null, TreeNull.class);
      assertSame(TreeNull.INSTANCE, result);

      result = TreePrimitive.convert("null", TreeNull.class);
      assertSame(TreeNull.INSTANCE, result);

      result = TreePrimitive.convert("NULL", TreeNull.class);
      assertSame(TreeNull.INSTANCE, result);
    }

    @Test
    void shouldThrowWhenConvertingNullToNonTreeNull() {
      assertThrows(
          IllegalArgumentException.class, () -> TreePrimitive.convert(null, TreeString.class));
    }

    @Test
    void shouldConvertToTreeBool() {
      // From Boolean
      TreeBool result = TreePrimitive.convert(true, TreeBool.class);
      assertEquals(true, result.getValue());

      // From String
      result = TreePrimitive.convert("true", TreeBool.class);
      assertEquals(true, result.getValue());

      result = TreePrimitive.convert("false", TreeBool.class);
      assertEquals(false, result.getValue());

      result = TreePrimitive.convert("invalid", TreeBool.class);
      assertEquals(
          false, result.getValue()); // Boolean.parseBoolean returns false for invalid strings

      // From Number
      result = TreePrimitive.convert(0, TreeBool.class);
      assertEquals(false, result.getValue());

      result = TreePrimitive.convert(1, TreeBool.class);
      assertEquals(true, result.getValue());

      result = TreePrimitive.convert(0.0, TreeBool.class);
      assertEquals(false, result.getValue());

      result = TreePrimitive.convert(3.14, TreeBool.class);
      assertEquals(true, result.getValue());
    }

    @Test
    void shouldThrowWhenConvertingUnsupportedTypeToTreeBool() {
      assertThrows(
          IllegalArgumentException.class,
          () -> TreePrimitive.convert(new Object(), TreeBool.class));
    }

    @Test
    void shouldConvertToTreeString() {
      TreeString result = TreePrimitive.convert(42, TreeString.class);
      assertEquals("42", result.getValue());

      result = TreePrimitive.convert(true, TreeString.class);
      assertEquals("true", result.getValue());

      result = TreePrimitive.convert(3.14, TreeString.class);
      assertEquals("3.14", result.getValue());
    }

    @Test
    void shouldConvertToTreeNumber() {
      // From Number
      TreeNumber result = TreePrimitive.convert(42, TreeNumber.class);
      assertEquals(42, result.getValue().intValue());

      result = TreePrimitive.convert(3.14, TreeNumber.class);
      assertEquals(3.14, result.getValue().doubleValue(), 0.001);

      // From String
      result = TreePrimitive.convert("42", TreeNumber.class);
      assertEquals(42, result.getValue().intValue());

      result = TreePrimitive.convert("3.14", TreeNumber.class);
      assertEquals(3.14, result.getValue().doubleValue(), 0.001);

      // From Boolean
      result = TreePrimitive.convert(true, TreeNumber.class);
      assertEquals(1, result.getValue().intValue());

      result = TreePrimitive.convert(false, TreeNumber.class);
      assertEquals(0, result.getValue().intValue());
    }

    @Test
    void shouldThrowWhenConvertingInvalidStringToTreeNumber() {
      assertThrows(
          IllegalArgumentException.class,
          () -> TreePrimitive.convert("not-a-number", TreeNumber.class));
    }

    @Test
    void shouldThrowWhenConvertingUnsupportedTypeToTreeNumber() {
      assertThrows(
          IllegalArgumentException.class,
          () -> TreePrimitive.convert(new Object(), TreeNumber.class));
    }

    @Test
    void shouldConvertToTreeList() {
      // From Iterable
      List<Object> input = Arrays.asList("hello", 42, true);
      TreeList result = TreePrimitive.convert(input, TreeList.class);

      assertEquals(3, result.size());
      assertEquals("hello", result.get(0).getValue());
      assertEquals(42, ((TreeNumber) result.get(1)).getValue().intValue());
      assertEquals(true, result.get(2).getValue());

      // From single object
      result = TreePrimitive.convert("single", TreeList.class);
      assertEquals(1, result.size());
      assertEquals("single", result.get(0).getValue());
    }

    @Test
    void shouldThrowForUnsupportedTargetType() {
      // Since TreeNodeIdentifier is abstract, we can't convert to it directly
      assertThrows(
          IllegalArgumentException.class,
          () -> TreePrimitive.convert("test", TreeNodeIdentifier.class));
    }

    @Test
    void shouldHandleJsonNodeInConvert() {
      JsonNode textNode = nodeFactory.textNode("test");
      TreeString result = TreePrimitive.convert(textNode, TreeString.class);
      assertEquals("test", result.getValue());

      JsonNode numberNode = nodeFactory.numberNode(42);
      TreeNumber numberResult = TreePrimitive.convert(numberNode, TreeNumber.class);
      assertEquals(42, numberResult.getValue().intValue());
    }

    @Test
    void shouldHandleTreePrimitiveInputInConvert() {
      TreeString input = new TreeString("test");
      TreeString result = TreePrimitive.convert(input, TreeString.class);
      assertEquals("test", result.getValue());

      // Convert TreeString to TreeNumber
      TreeString stringInput = new TreeString("42");
      TreeNumber numberResult = TreePrimitive.convert(stringInput, TreeNumber.class);
      assertEquals(42, numberResult.getValue().intValue());
    }
  }

  @Nested
  class EdgeCasesAndIntegrationTests {

    @Test
    void shouldHandleNestedListsInCreateTreePrimitive() {
      List<Object> nestedList =
          Arrays.asList(Arrays.asList("nested", 1), "top-level", Arrays.asList(true, false));

      TreePrimitive result = TreePrimitive.createTreePrimitive(nestedList);
      assertInstanceOf(TreeList.class, result);

      TreeList list = (TreeList) result;
      assertEquals(3, list.size());

      // First element should be a nested list
      assertInstanceOf(TreeList.class, list.get(0));
      TreeList nested = (TreeList) list.get(0);
      assertEquals(2, nested.size());
      assertEquals("nested", nested.get(0).getValue());
      assertEquals(1, ((TreeNumber) nested.get(1)).getValue().intValue());
    }

    @Test
    void shouldHandleEmptyListInConvert() {
      List<Object> emptyList = Arrays.asList();
      TreeList result = TreePrimitive.convert(emptyList, TreeList.class);
      assertEquals(0, result.size());
    }

    @Test
    void shouldConvertComplexJsonStructure() throws Exception {
      String complexJson =
          """
          {
            "string": "test",
            "number": 42,
            "boolean": true,
            "null": null,
            "array": [1, 2, 3]
          }
          """;

      JsonNode jsonNode = objectMapper.readTree(complexJson);

      // Test individual fields
      TreeString stringResult = TreePrimitive.convert(jsonNode.get("string"), TreeString.class);
      assertEquals("test", stringResult.getValue());

      TreeNumber numberResult = TreePrimitive.convert(jsonNode.get("number"), TreeNumber.class);
      assertEquals(42, numberResult.getValue().intValue());

      TreeBool boolResult = TreePrimitive.convert(jsonNode.get("boolean"), TreeBool.class);
      assertEquals(true, boolResult.getValue());

      TreeNull nullResult = TreePrimitive.convert(jsonNode.get("null"), TreeNull.class);
      assertSame(TreeNull.INSTANCE, nullResult);

      TreeList arrayResult = TreePrimitive.convert(jsonNode.get("array"), TreeList.class);
      assertEquals(3, arrayResult.size());
    }
  }
}
