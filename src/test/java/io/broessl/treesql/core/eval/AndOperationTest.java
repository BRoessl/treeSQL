package io.broessl.treesql.core.eval;

import org.junit.jupiter.api.Test;

import io.broessl.treesql.core.types.TreeBool;

import static org.junit.jupiter.api.Assertions.*;

public class AndOperationTest {
    private final AndOperation op = new AndOperation();

    @Test
    void testAndTrueTrue() {
        assertTrue(((TreeBool) op.call(new TreeBool[]{new TreeBool(true), new TreeBool(true)})).nativeValue());
    }

    @Test
    void testAndTrueFalse() {
        assertFalse(((TreeBool) op.call(new TreeBool[]{new TreeBool(true), new TreeBool(false)})).nativeValue());
    }

    @Test
    void testAndFalseTrue() {
        assertFalse(((TreeBool) op.call(new TreeBool[]{new TreeBool(false), new TreeBool(true)})).nativeValue());
    }

    @Test
    void testAndFalseFalse() {
        assertFalse(((TreeBool) op.call(new TreeBool[]{new TreeBool(false), new TreeBool(false)})).nativeValue());
    }

    @Test
    void testInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> op.call(new TreeBool[]{}));
        assertThrows(IllegalArgumentException.class, () -> op.call(null));
    }
}
