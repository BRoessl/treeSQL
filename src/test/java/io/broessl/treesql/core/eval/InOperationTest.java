package io.broessl.treesql.core.eval;

import org.junit.jupiter.api.Test;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InOperationTest {
    private final InOperation op = new InOperation();

    @Test
    void testInListTrue() {
        TreePrimitive a = new TreeNumber(5);
        TreePrimitive b = new TreeList(List.of(new TreeNumber(1), new TreeNumber(5), new TreeNumber(10)));
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertTrue(((TreeBool) result).nativeValue());
    }

    @Test
    void testInListFalse() {
        TreePrimitive a = new TreeString("foo");
        TreePrimitive b = new TreeList(List.of(new TreeString("bar"), new TreeString("baz")));
        TreePrimitive result = op.call(new TreePrimitive[]{a, b});
        assertTrue(result instanceof TreeBool);
        assertFalse(((TreeBool) result).nativeValue());
    }

    @Test
    void testInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> op.call(new TreePrimitive[]{}));
        assertThrows(IllegalArgumentException.class, () -> op.call(null));
        assertThrows(IllegalArgumentException.class, () -> op.call(new TreePrimitive[]{new TreeNumber(1), new TreeNumber(2)}));
    }
}
