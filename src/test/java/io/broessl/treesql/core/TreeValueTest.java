package io.broessl.treesql.core;

import org.junit.jupiter.api.Test;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeFullPath;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeRangedJSONPointer;
import io.broessl.treesql.core.types.TreeRangedLiteral;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import io.broessl.treesql.core.types.TreeValueAt;

import org.junit.jupiter.api.Assertions;


class TreeValueTest {

    @Test
    void testParseNumberValid() {
        TreeNumber num = TreeValue.parseNumber("123.45");
        Assertions.assertEquals("123.45", num.nativeValue().toString());
    }

    @Test
    void testParseNumberInvalid() {
        Assertions.assertThrows(NumberFormatException.class, () -> TreeValue.parseNumber("abc"));
    }

    @Test
    void testParseStringValid() {
        TreeString str = TreeValue.parseString("'hello'");
        Assertions.assertEquals("hello", str.nativeValue());
    }

    @Test
    void testParseStringEscapedQuotes() {
        TreeString str = TreeValue.parseString("''");
        Assertions.assertEquals("", str.nativeValue());
    }

    @Test
    void testParseStringInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parseString("hello"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parseString("'hello"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parseString("hello'"));
    }

    @Test
    void testParseBooleanTrue() {
        TreeBool bool = TreeValue.parseBoolean("TRUE");
        Assertions.assertTrue(bool.nativeValue());
    }

    @Test
    void testParseBooleanFalse() {
        TreeBool bool = TreeValue.parseBoolean("FALSE");
        Assertions.assertFalse(bool.nativeValue());
    }

    @Test
    void testParseBooleanInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parseBoolean("yes"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parseBoolean("true"));
    }

    @Test
    void testParseNullValid() {
        TreeNull n = TreeValue.parseNull("NULL");
        Assertions.assertSame(TreeNull.INSTANCE, n);
    }

    @Test
    void testParseNullInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parseNull("null"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parseNull("none"));
    }

    @Test
    void testParseRangedLiteral() {
        TreeRangedLiteral lit = TreeValue.parseRangedLiteral("foo");
        Assertions.assertEquals("foo", lit.toString());
    }

    @Test
    void testParseRangedJSONPointerValid() {
        TreeRangedJSONPointer ptr = TreeValue.parseRangedJSONPointer("\"/foo/bar\"");
        Assertions.assertEquals("/foo/bar", ptr.toString());
    }

    @Test
    void testParseRangedJSONPointerInvalidNotAsJsonString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parseRangedJSONPointer("/foo/bar"));
    }

    @Test
    void testParseValueAtValid() {
        TreeValueAt at = TreeValue.parseValueAt("@foo");
        Assertions.assertEquals("@foo", at.toString());
    }

    @Test
    void testParseValueAtInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parseValueAt("foo"));
    }

    @Test
    void testParsePathVariableValid() {
        TreeFullPath path = TreeValue.parsePathVariable("abc");
        Assertions.assertEquals("abc", path.toString());
    }

    @Test
    void testParsePathVariableInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parsePathVariable("Abc"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parsePathVariable("ABC"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> TreeValue.parsePathVariable("abcD"));
    }
}