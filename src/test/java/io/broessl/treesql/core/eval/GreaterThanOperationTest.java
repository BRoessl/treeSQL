package io.broessl.treesql.core.eval;

import org.junit.jupiter.api.Test;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;

import static org.junit.jupiter.api.Assertions.*;

public class GreaterThanOperationTest {
    private final GreaterOperation op = new GreaterOperation();

    @Test
    void testGreaterThanNumbers() {
        TreePrimitive a = new TreeNumber(5);
        TreePrimitive b = new TreeNumber(2);
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertTrue(((TreeBool) result).nativeValue());
    }

    @Test
    void testNotGreaterThanNumbers() {
        TreePrimitive a = new TreeNumber(1);
        TreePrimitive b = new TreeNumber(2);
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertFalse(((TreeBool) result).nativeValue());
    }

    @Test
    void testGreaterThanStrings() {
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
