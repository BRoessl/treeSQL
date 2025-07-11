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
      TreeValue result = TreeValue.createTreePrimitive(original);
      assertSame(original, result);
    }

    @Test
    void shouldCreateTreeNullForNull() {
      TreeValue result = TreeValue.createTreePrimitive(null);
      assertSame(TreeNull.INSTANCE, result);
    }

    @Test
    void shouldCreateTreeBoolForBoolean() {
      TreeValue result = TreeValue.createTreePrimitive(true);
      assertInstanceOf(TreeBool.class, result);
      assertEquals(true, result.getValue());

      result = TreeValue.createTreePrimitive(false);
      assertInstanceOf(TreeBool.class, result);
      assertEquals(false, result.getValue());
    }

    @Test
    void shouldCreateTreeStringForString() {
      TreeValue result = TreeValue.createTreePrimitive("hello");
      assertInstanceOf(TreeString.class, result);
      assertEquals("hello", result.getValue());
    }

    @Test
    void shouldCreateTreeNumberForNumbers() {
      TreeValue result = TreeValue.createTreePrimitive(42);
      assertInstanceOf(TreeNumber.class, result);
      assertEquals(42, ((TreeNumber) result).getValue().intValue());

      result = TreeValue.createTreePrimitive(3.14);
      assertInstanceOf(TreeNumber.class, result);
      assertEquals(3.14, ((TreeNumber) result).getValue().doubleValue(), 0.001);
    }

    @Test
    void shouldCreateTreeListForList() {
      List<Object> input = Arrays.asList("hello", 42, true);
      TreeValue result = TreeValue.createTreePrimitive(input);

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
      TreeValue result = TreeValue.createTreePrimitive(nullNode);
      assertSame(TreeNull.INSTANCE, result);
    }

    @Test
    void shouldHandleJsonNodeMissing() {
      JsonNode missingNode = nodeFactory.missingNode();
      TreeValue result = TreeValue.createTreePrimitive(missingNode);
      assertSame(TreeNull.INSTANCE, result);
    }

    @Test
    void shouldHandleJsonNodeText() {
      JsonNode textNode = nodeFactory.textNode("test");
      TreeValue result = TreeValue.createTreePrimitive(textNode);
      assertInstanceOf(TreeString.class, result);
      assertEquals("test", result.getValue());
    }

    @Test
    void shouldHandleJsonNodeBoolean() {
      JsonNode boolNode = nodeFactory.booleanNode(true);
      TreeValue result = TreeValue.createTreePrimitive(boolNode);
      assertInstanceOf(TreeBool.class, result);
      assertEquals(true, result.getValue());
    }

    @Test
    void shouldHandleJsonNodeNumber() {
      JsonNode numberNode = nodeFactory.numberNode(42);
      TreeValue result = TreeValue.createTreePrimitive(numberNode);
      assertInstanceOf(TreeNumber.class, result);
      assertEquals(42, ((TreeNumber) result).getValue().intValue());
    }

    @Test
    void shouldHandleJsonNodeArray() throws Exception {
      String jsonArray = "[\"hello\", 42, true]";
      ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(jsonArray);

      TreeValue result = TreeValue.createTreePrimitive(arrayNode);
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
          IllegalArgumentException.class, () -> TreeValue.createTreePrimitive(unsupported));
    }
  }

  @Nested
  class ConvertTests {

    @Test
    void shouldConvertToTreeNull() {
      TreeNull result = TreeValue.convert(null, TreeNull.class);
      assertSame(TreeNull.INSTANCE, result);

      result = TreeValue.convert("null", TreeNull.class);
      assertSame(TreeNull.INSTANCE, result);

      result = TreeValue.convert("NULL", TreeNull.class);
      assertSame(TreeNull.INSTANCE, result);
    }

    @Test
    void shouldThrowWhenConvertingNullToNonTreeNull() {
      assertThrows(IllegalArgumentException.class, () -> TreeValue.convert(null, TreeString.class));
    }

    @Test
    void shouldConvertToTreeBool() {
      // From Boolean
      TreeBool result = TreeValue.convert(true, TreeBool.class);
      assertEquals(true, result.getValue());

      // From String
      result = TreeValue.convert("true", TreeBool.class);
      assertEquals(true, result.getValue());

      result = TreeValue.convert("false", TreeBool.class);
      assertEquals(false, result.getValue());

      result = TreeValue.convert("invalid", TreeBool.class);
      assertEquals(
          false, result.getValue()); // Boolean.parseBoolean returns false for invalid strings

      // From Number
      result = TreeValue.convert(0, TreeBool.class);
      assertEquals(false, result.getValue());

      result = TreeValue.convert(1, TreeBool.class);
      assertEquals(true, result.getValue());

      result = TreeValue.convert(0.0, TreeBool.class);
      assertEquals(false, result.getValue());

      result = TreeValue.convert(3.14, TreeBool.class);
      assertEquals(true, result.getValue());
    }

    @Test
    void shouldThrowWhenConvertingUnsupportedTypeToTreeBool() {
      assertThrows(
          IllegalArgumentException.class, () -> TreeValue.convert(new Object(), TreeBool.class));
    }

    @Test
    void shouldConvertToTreeString() {
      TreeString result = TreeValue.convert(42, TreeString.class);
      assertEquals("42", result.getValue());

      result = TreeValue.convert(true, TreeString.class);
      assertEquals("true", result.getValue());

      result = TreeValue.convert(3.14, TreeString.class);
      assertEquals("3.14", result.getValue());
    }

    @Test
    void shouldConvertToTreeNumber() {
      // From Number
      TreeNumber result = TreeValue.convert(42, TreeNumber.class);
      assertEquals(42, result.getValue().intValue());

      result = TreeValue.convert(3.14, TreeNumber.class);
      assertEquals(3.14, result.getValue().doubleValue(), 0.001);

      // From String
      result = TreeValue.convert("42", TreeNumber.class);
      assertEquals(42, result.getValue().intValue());

      result = TreeValue.convert("3.14", TreeNumber.class);
      assertEquals(3.14, result.getValue().doubleValue(), 0.001);

      // From Boolean
      result = TreeValue.convert(true, TreeNumber.class);
      assertEquals(1, result.getValue().intValue());

      result = TreeValue.convert(false, TreeNumber.class);
      assertEquals(0, result.getValue().intValue());
    }

    @Test
    void shouldThrowWhenConvertingInvalidStringToTreeNumber() {
      assertThrows(
          IllegalArgumentException.class,
          () -> TreeValue.convert("not-a-number", TreeNumber.class));
    }

    @Test
    void shouldThrowWhenConvertingUnsupportedTypeToTreeNumber() {
      assertThrows(
          IllegalArgumentException.class, () -> TreeValue.convert(new Object(), TreeNumber.class));
    }

    @Test
    void shouldConvertToTreeList() {
      // From Iterable
      List<Object> input = Arrays.asList("hello", 42, true);
      TreeList result = TreeValue.convert(input, TreeList.class);

      assertEquals(3, result.size());
      assertEquals("hello", result.get(0).getValue());
      assertEquals(42, ((TreeNumber) result.get(1)).getValue().intValue());
      assertEquals(true, result.get(2).getValue());

      // From single object
      result = TreeValue.convert("single", TreeList.class);
      assertEquals(1, result.size());
      assertEquals("single", result.get(0).getValue());
    }

    @Test
    void shouldThrowForUnsupportedTargetType() {
      // Since TreeNodeIdentifier is abstract, we can't convert to it directly
      assertThrows(
          IllegalArgumentException.class,
          () -> TreeValue.convert("test", TreeNodeIdentifier.class));
    }

    @Test
    void shouldHandleJsonNodeInConvert() {
      JsonNode textNode = nodeFactory.textNode("test");
      TreeString result = TreeValue.convert(textNode, TreeString.class);
      assertEquals("test", result.getValue());

      JsonNode numberNode = nodeFactory.numberNode(42);
      TreeNumber numberResult = TreeValue.convert(numberNode, TreeNumber.class);
      assertEquals(42, numberResult.getValue().intValue());
    }

    @Test
    void shouldHandleTreePrimitiveInputInConvert() {
      TreeString input = new TreeString("test");
      TreeString result = TreeValue.convert(input, TreeString.class);
      assertEquals("test", result.getValue());

      // Convert TreeString to TreeNumber
      TreeString stringInput = new TreeString("42");
      TreeNumber numberResult = TreeValue.convert(stringInput, TreeNumber.class);
      assertEquals(42, numberResult.getValue().intValue());
    }
  }

  @Nested
  class EdgeCasesAndIntegrationTests {

    @Test
    void shouldHandleNestedListsInCreateTreePrimitive() {
      List<Object> nestedList =
          Arrays.asList(Arrays.asList("nested", 1), "top-level", Arrays.asList(true, false));

      TreeValue result = TreeValue.createTreePrimitive(nestedList);
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
      TreeList result = TreeValue.convert(emptyList, TreeList.class);
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
      TreeString stringResult = TreeValue.convert(jsonNode.get("string"), TreeString.class);
      assertEquals("test", stringResult.getValue());

      TreeNumber numberResult = TreeValue.convert(jsonNode.get("number"), TreeNumber.class);
      assertEquals(42, numberResult.getValue().intValue());

      TreeBool boolResult = TreeValue.convert(jsonNode.get("boolean"), TreeBool.class);
      assertEquals(true, boolResult.getValue());

      TreeNull nullResult = TreeValue.convert(jsonNode.get("null"), TreeNull.class);
      assertSame(TreeNull.INSTANCE, nullResult);

      TreeList arrayResult = TreeValue.convert(jsonNode.get("array"), TreeList.class);
      assertEquals(3, arrayResult.size());
    }
  }
}
