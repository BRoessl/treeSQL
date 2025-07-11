package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeBool;
import org.junit.jupiter.api.Test;

public class OrOperationTest {
  private final OrOperation op = new OrOperation();

  @Test
  void testOrTrueTrue() {
    assertTrue(
        ((TreeBool) op.call(new TreeBool[] {new TreeBool(true), new TreeBool(true)})).getValue());
  }

  @Test
  void testOrTrueFalse() {
    assertTrue(
        ((TreeBool) op.call(new TreeBool[] {new TreeBool(true), new TreeBool(false)})).getValue());
  }

  @Test
  void testOrFalseTrue() {
    assertTrue(
        ((TreeBool) op.call(new TreeBool[] {new TreeBool(false), new TreeBool(true)})).getValue());
  }

  @Test
  void testOrFalseFalse() {
    assertFalse(
        ((TreeBool) op.call(new TreeBool[] {new TreeBool(false), new TreeBool(false)})).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> op.call(new TreeBool[] {}));
    assertThrows(IllegalArgumentException.class, () -> op.call(null));
  }
}
