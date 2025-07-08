package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
import org.junit.jupiter.api.Test;

public class AddOperationTest {
  private final AddOperation addOp = new AddOperation();

  @Test
  void testAddNumbers() {
    TreePrimitive a = new TreeNumber(2);
    TreePrimitive b = new TreeNumber(3);
    TreePrimitive result = addOp.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeNumber);
    assertEquals(5.0, ((TreeNumber) result).getValue().doubleValue());
  }

  @Test
  void testAddStrings() {
    TreePrimitive a = new TreeString("foo");
    TreePrimitive b = new TreeString("bar");
    TreePrimitive result = addOp.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeString);
    assertEquals("foobar", ((TreeString) result).getValue());
  }

  @Test
  void testAddNumberAndString() {
    TreePrimitive a = new TreeNumber(7);
    TreePrimitive b = new TreeString(" apples");
    TreePrimitive result = addOp.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeString);
    assertEquals("7 apples", ((TreeString) result).getValue());
  }

  @Test
  void testAddStringAndNumber() {
    TreePrimitive a = new TreeString("Count: ");
    TreePrimitive b = new TreeNumber(42);
    TreePrimitive result = addOp.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeString);
    assertEquals("Count: 42", ((TreeString) result).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> addOp.call(new TreePrimitive[] {}));
    assertThrows(IllegalArgumentException.class, () -> addOp.call(null));
  }
}
