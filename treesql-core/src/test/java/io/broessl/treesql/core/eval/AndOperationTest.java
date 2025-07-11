package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeBool;
import org.junit.jupiter.api.Test;

public class AndOperationTest {
  private final AndOperation op = new AndOperation();

  @Test
  void testAndTrueTrue() {
    assertTrue(
        ((TreeBool) op.call(new TreeBool[] {new TreeBool(true), new TreeBool(true)})).getValue());
  }

  @Test
  void testAndTrueFalse() {
    assertFalse(
        ((TreeBool) op.call(new TreeBool[] {new TreeBool(true), new TreeBool(false)})).getValue());
  }

  @Test
  void testAndFalseTrue() {
    assertFalse(
        ((TreeBool) op.call(new TreeBool[] {new TreeBool(false), new TreeBool(true)})).getValue());
  }

  @Test
  void testAndFalseFalse() {
    assertFalse(
        ((TreeBool) op.call(new TreeBool[] {new TreeBool(false), new TreeBool(false)})).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> op.call(new TreeBool[] {}));
    assertThrows(IllegalArgumentException.class, () -> op.call(null));
  }
}
