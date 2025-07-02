package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeBool;
import org.junit.jupiter.api.Test;

public class NotOperationTest {
  private final NotOperation op = new NotOperation();

  @Test
  void testNotTrue() {
    assertFalse(((TreeBool) op.call(new TreeBool[] {new TreeBool(true)})).nativeValue());
  }

  @Test
  void testNotFalse() {
    assertTrue(((TreeBool) op.call(new TreeBool[] {new TreeBool(false)})).nativeValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> op.call(new TreeBool[] {}));
    assertThrows(IllegalArgumentException.class, () -> op.call(null));
  }
}
