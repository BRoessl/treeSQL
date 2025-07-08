package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeValue;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class MultiplyOperationTest {
  private final MultiplyOperation mulOp = new MultiplyOperation();

  @Test
  void testMultiplyNumbers() {
    TreeValue a = new TreeNumber(4);
    TreeValue b = new TreeNumber(5);
    TreeValue result = mulOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeNumber);
    assertEquals(BigDecimal.valueOf(20), ((TreeNumber) result).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> mulOp.call(new TreeValue[] {}));
    assertThrows(IllegalArgumentException.class, () -> mulOp.call(null));
  }
}
