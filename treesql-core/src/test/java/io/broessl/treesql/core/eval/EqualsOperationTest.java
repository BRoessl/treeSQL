package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import org.junit.jupiter.api.Test;

public class EqualsOperationTest {
  private final EqualsOperation equalsOp = new EqualsOperation();

  @Test
  void testEqualNumbers() {
    TreeValue a = new TreeNumber(5);
    TreeValue b = new TreeNumber(5);
    TreeValue result = equalsOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).getValue());
  }

  @Test
  void testNotEqualNumbers() {
    TreeValue a = new TreeNumber(5);
    TreeValue b = new TreeNumber(7);
    TreeValue result = equalsOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertFalse(((TreeBool) result).getValue());
  }

  @Test
  void testEqualStrings() {
    TreeValue a = new TreeString("foo");
    TreeValue b = new TreeString("foo");
    TreeValue result = equalsOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).getValue());
  }

  @Test
  void testNotEqualStrings() {
    TreeValue a = new TreeString("foo");
    TreeValue b = new TreeString("bar");
    TreeValue result = equalsOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertFalse(((TreeBool) result).getValue());
  }

  @Test
  void testDifferentTypes() {
    TreeValue a = new TreeNumber(1);
    TreeValue b = new TreeString("1");
    TreeValue result = equalsOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertFalse(((TreeBool) result).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> equalsOp.call(new TreeValue[] {}));
    assertThrows(IllegalArgumentException.class, () -> equalsOp.call(null));
  }
}
