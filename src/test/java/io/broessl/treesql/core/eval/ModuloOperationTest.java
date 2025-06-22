package io.broessl.treesql.core.eval;

import org.junit.jupiter.api.Test;

import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ModuloOperationTest {
    private final ModuloOperation op = new ModuloOperation();

    @Test
    void testModuloNumbers() {
        TreePrimitive a = new TreeNumber(10);
        TreePrimitive b = new TreeNumber(3);
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeNumber);
        assertEquals(BigDecimal.valueOf(1), ((TreeNumber) result).nativeValue());
    }

    @Test
    void testModuloByZero() {
        TreePrimitive a = new TreeNumber(10);
        TreePrimitive b = new TreeNumber(0);
        assertThrows(ArithmeticException.class, () -> op.call(new TreePrimitive[]{a, b}));
    }

    @Test
    void testInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> op.call(new TreePrimitive[]{}));
        assertThrows(IllegalArgumentException.class, () -> op.call(null));
    }
}
