package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import org.junit.jupiter.api.Test;

public class NotMatchOperationTest {
  private final NotMatchOperation op = new NotMatchOperation();

  @Test
  void testNotMatchTrue() {
    TreeValue a = new TreeString("hello");
    TreeValue b = new TreeString("\\d+");
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).getValue());
  }

  @Test
  void testNotMatchFalse() {
    TreeValue a = new TreeString("hello123");
    TreeValue b = new TreeString("hello\\d+");
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertFalse(((TreeBool) result).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> op.call(new TreeValue[] {}));
    assertThrows(IllegalArgumentException.class, () -> op.call(null));
    assertThrows(
        IllegalArgumentException.class,
        () -> op.call(new TreeValue[] {new TreeString("foo"), null}));
  }
}
