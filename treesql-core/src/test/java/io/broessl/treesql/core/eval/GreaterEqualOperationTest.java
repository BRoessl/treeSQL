package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import org.junit.jupiter.api.Test;

public class GreaterEqualOperationTest {
  private final GreaterEqualOperation op = new GreaterEqualOperation();

  @Test
  void testGreaterEqualThanNumbers() {
    TreeValue a = new TreeNumber(2);
    TreeValue b = new TreeNumber(2);
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).getValue());
  }

  @Test
  void testGreaterEqualThanNumbersTrue() {
    TreeValue a = new TreeNumber(3);
    TreeValue b = new TreeNumber(2);
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).getValue());
  }

  @Test
  void testGreaterEqualThanNumbersFalse() {
    TreeValue a = new TreeNumber(1);
    TreeValue b = new TreeNumber(2);
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertFalse(((TreeBool) result).getValue());
  }

  @Test
  void testGreaterEqualThanStrings() {
    TreeValue a = new TreeString("zebra");
    TreeValue b = new TreeString("apple");
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> op.call(new TreeValue[] {}));
    assertThrows(IllegalArgumentException.class, () -> op.call(null));
  }
}
