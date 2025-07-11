package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import org.junit.jupiter.api.Test;

public class AddOperationTest {
  private final AddOperation addOp = new AddOperation();

  @Test
  void testAddNumbers() {
    TreeValue a = new TreeNumber(2);
    TreeValue b = new TreeNumber(3);
    TreeValue result = addOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeNumber);
    assertEquals(5.0, ((TreeNumber) result).getValue().doubleValue());
  }

  @Test
  void testAddStrings() {
    TreeValue a = new TreeString("foo");
    TreeValue b = new TreeString("bar");
    TreeValue result = addOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeString);
    assertEquals("foobar", ((TreeString) result).getValue());
  }

  @Test
  void testAddNumberAndString() {
    TreeValue a = new TreeNumber(7);
    TreeValue b = new TreeString(" apples");
    TreeValue result = addOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeString);
    assertEquals("7 apples", ((TreeString) result).getValue());
  }

  @Test
  void testAddStringAndNumber() {
    TreeValue a = new TreeString("Count: ");
    TreeValue b = new TreeNumber(42);
    TreeValue result = addOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeString);
    assertEquals("Count: 42", ((TreeString) result).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> addOp.call(new TreeValue[] {}));
    assertThrows(IllegalArgumentException.class, () -> addOp.call(null));
  }
}
