package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
import org.junit.jupiter.api.Test;

public class LessThanOperationTest {
  private final LessOperation op = new LessOperation();

  @Test
  void testLessThanNumbers() {
    TreePrimitive a = new TreeNumber(1);
    TreePrimitive b = new TreeNumber(2);
    TreePrimitive result = op.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).nativeValue());
  }

  @Test
  void testNotLessThanNumbers() {
    TreePrimitive a = new TreeNumber(3);
    TreePrimitive b = new TreeNumber(2);
    TreePrimitive result = op.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertFalse(((TreeBool) result).nativeValue());
  }

  @Test
  void testLessThanStrings() {
    TreePrimitive a = new TreeString("apple");
    TreePrimitive b = new TreeString("banana");
    TreePrimitive result = op.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).nativeValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> op.call(new TreePrimitive[] {}));
    assertThrows(IllegalArgumentException.class, () -> op.call(null));
  }
}
