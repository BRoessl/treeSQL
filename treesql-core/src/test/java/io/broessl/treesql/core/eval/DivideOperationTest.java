package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class DivideOperationTest {
  private final DivideOperation divOp = new DivideOperation();

  @Test
  void testDivideNumbers() {
    TreePrimitive a = new TreeNumber(10);
    TreePrimitive b = new TreeNumber(2.5);
    TreePrimitive result = divOp.call(new TreePrimitive[] {a, b});
    assertTrue(result instanceof TreeNumber);
    assertEquals(BigDecimal.valueOf(4), ((TreeNumber) result).getValue().stripTrailingZeros());
  }

  @Test
  void testDivideByZero() {
    TreePrimitive a = new TreeNumber(10);
    TreePrimitive b = new TreeNumber(0);
    assertThrows(ArithmeticException.class, () -> divOp.call(new TreePrimitive[] {a, b}));
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> divOp.call(new TreePrimitive[] {}));
    assertThrows(IllegalArgumentException.class, () -> divOp.call(null));
  }
}
