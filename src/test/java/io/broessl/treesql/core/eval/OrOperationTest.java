package io.broessl.treesql.core.eval;

import org.junit.jupiter.api.Test;

import io.broessl.treesql.core.types.TreeBool;

import static org.junit.jupiter.api.Assertions.*;

public class OrOperationTest {
    private final OrOperation op = new OrOperation();

    @Test
    void testOrTrueTrue() {
        assertTrue(((TreeBool) op.call(new TreeBool[]{new TreeBool(true), new TreeBool(true)})).nativeValue());
    }

    @Test
    void testOrTrueFalse() {
        assertTrue(((TreeBool) op.call(new TreeBool[]{new TreeBool(true), new TreeBool(false)})).nativeValue());
    }

    @Test
    void testOrFalseTrue() {
        assertTrue(((TreeBool) op.call(new TreeBool[]{new TreeBool(false), new TreeBool(true)})).nativeValue());
    }

    @Test
    void testOrFalseFalse() {
        assertFalse(((TreeBool) op.call(new TreeBool[]{new TreeBool(false), new TreeBool(false)})).nativeValue());
    }

    @Test
    void testInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> op.call(new TreeBool[]{}));
        assertThrows(IllegalArgumentException.class, () -> op.call(null));
    }
}
