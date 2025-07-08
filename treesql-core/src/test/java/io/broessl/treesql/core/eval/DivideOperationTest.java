package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeValue;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class DivideOperationTest {
  private final DivideOperation divOp = new DivideOperation();

  @Test
  void testDivideNumbers() {
    TreeValue a = new TreeNumber(10);
    TreeValue b = new TreeNumber(2.5);
    TreeValue result = divOp.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeNumber);
    assertEquals(BigDecimal.valueOf(4), ((TreeNumber) result).getValue().stripTrailingZeros());
  }

  @Test
  void testDivideByZero() {
    TreeValue a = new TreeNumber(10);
    TreeValue b = new TreeNumber(0);
    assertThrows(ArithmeticException.class, () -> divOp.call(new TreeValue[] {a, b}));
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> divOp.call(new TreeValue[] {}));
    assertThrows(IllegalArgumentException.class, () -> divOp.call(null));
  }
}
