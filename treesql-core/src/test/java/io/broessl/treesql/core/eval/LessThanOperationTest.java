package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import org.junit.jupiter.api.Test;

public class LessThanOperationTest {
  private final LessOperation op = new LessOperation();

  @Test
  void testLessThanNumbers() {
    TreeValue a = new TreeNumber(1);
    TreeValue b = new TreeNumber(2);
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).getValue());
  }

  @Test
  void testNotLessThanNumbers() {
    TreeValue a = new TreeNumber(3);
    TreeValue b = new TreeNumber(2);
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertFalse(((TreeBool) result).getValue());
  }

  @Test
  void testLessThanStrings() {
    TreeValue a = new TreeString("apple");
    TreeValue b = new TreeString("banana");
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
