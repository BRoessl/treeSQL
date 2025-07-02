package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class SubtractOperationTest {
  private final SubtractOperation subOp = new SubtractOperation();

  @Test
  void testSubtractNumbers() {
    TreePrimitive a = new TreeNumber(10);
    TreePrimitive b = new TreeNumber(3);
    TreePrimitive result = subOp.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeNumber);
    assertEquals(BigDecimal.valueOf(7), ((TreeNumber) result).nativeValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> subOp.call(new TreePrimitive[] {}));
    assertThrows(IllegalArgumentException.class, () -> subOp.call(null));
  }
}
