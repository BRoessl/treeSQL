package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeValue;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class ModuloOperationTest {
  private final ModuloOperation op = new ModuloOperation();

  @Test
  void testModuloNumbers() {
    TreeValue a = new TreeNumber(10);
    TreeValue b = new TreeNumber(3);
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeNumber);
    assertEquals(BigDecimal.valueOf(1), ((TreeNumber) result).getValue());
  }

  @Test
  void testModuloByZero() {
    TreeValue a = new TreeNumber(10);
    TreeValue b = new TreeNumber(0);
    assertThrows(ArithmeticException.class, () -> op.call(new TreeValue[] {a, b}));
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> op.call(new TreeValue[] {}));
    assertThrows(IllegalArgumentException.class, () -> op.call(null));
  }
}
