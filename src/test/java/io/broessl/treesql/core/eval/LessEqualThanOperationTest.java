package io.broessl.treesql.core.eval;

import org.junit.jupiter.api.Test;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;

import static org.junit.jupiter.api.Assertions.*;

public class LessEqualThanOperationTest {
    private final LessEqualOperation op = new LessEqualOperation();

    @Test
    void testLessEqualThanNumbers() {
        TreePrimitive a = new TreeNumber(2);
        TreePrimitive b = new TreeNumber(2);
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertTrue(((TreeBool) result).nativeValue());
    }

    @Test
    void testLessEqualThanNumbersFalse() {
        TreePrimitive a = new TreeNumber(3);
        TreePrimitive b = new TreeNumber(2);
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertFalse(((TreeBool) result).nativeValue());
    }

    @Test
    void testLessEqualThanStrings() {
        TreePrimitive a = new TreeString("apple");
        TreePrimitive b = new TreeString("banana");
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
