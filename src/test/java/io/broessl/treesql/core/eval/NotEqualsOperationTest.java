package io.broessl.treesql.core.eval;

import org.junit.jupiter.api.Test;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;

import static org.junit.jupiter.api.Assertions.*;

public class NotEqualsOperationTest {
    private final NotEqualsOperation notEqualsOp = new NotEqualsOperation();

    @Test
    void testNotEqualNumbers() {
        TreePrimitive a = new TreeNumber(5);
        TreePrimitive b = new TreeNumber(7);
        TreePrimitive result = notEqualsOp.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertTrue(((TreeBool) result).nativeValue());
    }

    @Test
    void testEqualNumbers() {
        TreePrimitive a = new TreeNumber(5);
        TreePrimitive b = new TreeNumber(5);
        TreePrimitive result = notEqualsOp.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertFalse(((TreeBool) result).nativeValue());
    }

    @Test
    void testNotEqualStrings() {
        TreePrimitive a = new TreeString("foo");
        TreePrimitive b = new TreeString("bar");
        TreePrimitive result = notEqualsOp.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertTrue(((TreeBool) result).nativeValue());
    }

    @Test
    void testEqualStrings() {
        TreePrimitive a = new TreeString("foo");
        TreePrimitive b = new TreeString("foo");
        TreePrimitive result = notEqualsOp.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertFalse(((TreeBool) result).nativeValue());
    }

    @Test
    void testDifferentTypes() {
        TreePrimitive a = new TreeNumber(1);
        TreePrimitive b = new TreeString("1");
        TreePrimitive result = notEqualsOp.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertTrue(((TreeBool) result).nativeValue());
    }

    @Test
    void testInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> notEqualsOp.call(new TreePrimitive[]{}));
        assertThrows(IllegalArgumentException.class, () -> notEqualsOp.call(null));
    }
}
