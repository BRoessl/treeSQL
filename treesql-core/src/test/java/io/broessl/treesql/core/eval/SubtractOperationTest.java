package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeValue;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class SubtractOperationTest {
  private final SubtractOperation subOp = new SubtractOperation();

  @Test
  void testSubtractNumbers() {
    TreeValue a = new TreeNumber(10);
    TreeValue b = new TreeNumber(3);
    TreeValue result = subOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeNumber);
    assertEquals(BigDecimal.valueOf(7), ((TreeNumber) result).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> subOp.call(new TreeValue[] {}));
    assertThrows(IllegalArgumentException.class, () -> subOp.call(null));
  }
}
