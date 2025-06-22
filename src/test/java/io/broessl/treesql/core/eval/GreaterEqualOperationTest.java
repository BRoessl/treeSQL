package io.broessl.treesql.core.eval;

import org.junit.jupiter.api.Test;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;

import static org.junit.jupiter.api.Assertions.*;

public class GreaterEqualOperationTest {
    private final GreaterEqualOperation op = new GreaterEqualOperation();

    @Test
    void testGreaterEqualThanNumbers() {
        TreePrimitive a = new TreeNumber(2);
        TreePrimitive b = new TreeNumber(2);
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertTrue(((TreeBool) result).nativeValue());
    }

    @Test
    void testGreaterEqualThanNumbersTrue() {
        TreePrimitive a = new TreeNumber(3);
        TreePrimitive b = new TreeNumber(2);
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertTrue(((TreeBool) result).nativeValue());
    }

    @Test
    void testGreaterEqualThanNumbersFalse() {
        TreePrimitive a = new TreeNumber(1);
        TreePrimitive b = new TreeNumber(2);
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertFalse(((TreeBool) result).nativeValue());
    }

    @Test
    void testGreaterEqualThanStrings() {
        TreePrimitive a = new TreeString("zebra");
        TreePrimitive b = new TreeString("apple");
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertTrue(((TreeBool) result).nativeValue());
    }

    @Test
    void testInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> op.call(new TreePrimitive[]{}));
        assertThrows(IllegalArgumentException.class, () -> op.call(null));
    }
}
