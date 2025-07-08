package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class MultiplyOperationTest {
  private final MultiplyOperation mulOp = new MultiplyOperation();

  @Test
  void testMultiplyNumbers() {
    TreePrimitive a = new TreeNumber(4);
    TreePrimitive b = new TreeNumber(5);
    TreePrimitive result = mulOp.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeNumber);
    assertEquals(BigDecimal.valueOf(20), ((TreeNumber) result).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> mulOp.call(new TreePrimitive[] {}));
    assertThrows(IllegalArgumentException.class, () -> mulOp.call(null));
  }
}
