package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
import org.junit.jupiter.api.Test;

public class MatchOperationTest {
  private final MatchOperation op = new MatchOperation();

  @Test
  void testMatchTrue() {
    TreePrimitive a = new TreeString("hello123");
    TreePrimitive b = new TreeString("hello\\d+");
    TreePrimitive result = op.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).nativeValue());
  }

  @Test
  void testMatchFalse() {
    TreePrimitive a = new TreeString("hello");
    TreePrimitive b = new TreeString("\\d+");
    TreePrimitive result = op.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertFalse(((TreeBool) result).nativeValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> op.call(new TreePrimitive[] {}));
    assertThrows(IllegalArgumentException.class, () -> op.call(null));
    assertThrows(
        IllegalArgumentException.class,
        () -> op.call(new TreePrimitive[] {new TreeString("foo"), null}));
  }
}
