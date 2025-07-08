package io.broessl.treesql.core;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeFullPath;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeRangedJSONPointer;
import io.broessl.treesql.core.types.TreeRangedLiteral;
import io.broessl.treesql.core.types.TreeStackableValue;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValueAt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TreeValueTest {

  @Test
  void testParseNumberValid() {
    TreeNumber num = TreeStackableValue.parseNumber("123.45");
    Assertions.assertEquals("123.45", num.getValue().toString());
  }

  @Test
  void testParseNumberInvalid() {
    Assertions.assertThrows(
        NumberFormatException.class, () -> TreeStackableValue.parseNumber("abc"));
  }

  @Test
  void testParseStringValid() {
    TreeString str = TreeStackableValue.parseString("'hello'");
    Assertions.assertEquals("hello", str.getValue());
  }

  @Test
  void testParseStringEscapedQuotes() {
    TreeString str = TreeStackableValue.parseString("''");
    Assertions.assertEquals("", str.getValue());
  }

  @Test
  void testParseStringInvalid() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parseString("hello"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parseString("'hello"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parseString("hello'"));
  }

  @Test
  void testParseBooleanTrue() {
    TreeBool bool = TreeStackableValue.parseBoolean("TRUE");
    Assertions.assertTrue(bool.getValue());
  }

  @Test
  void testParseBooleanFalse() {
    TreeBool bool = TreeStackableValue.parseBoolean("FALSE");
    Assertions.assertFalse(bool.getValue());
  }

  @Test
  void testParseBooleanInvalid() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parseBoolean("yes"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parseBoolean("true"));
  }

  @Test
  void testParseNullValid() {
    TreeNull n = TreeStackableValue.parseNull("NULL");
    Assertions.assertSame(TreeNull.INSTANCE, n);
  }

  @Test
  void testParseNullInvalid() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parseNull("null"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parseNull("none"));
  }

  @Test
  void testParseRangedLiteral() {
    TreeRangedLiteral lit = TreeStackableValue.parseRangedLiteral("foo");
    Assertions.assertEquals("foo", lit.toString());
  }

  @Test
  void testParseRangedJSONPointerValid() {
    TreeRangedJSONPointer ptr = TreeStackableValue.parseRangedJSONPointer("\"/foo/bar\"");
    Assertions.assertEquals("/foo/bar", ptr.toString());
  }

  @Test
  void testParseRangedJSONPointerInvalidNotAsJsonString() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> TreeStackableValue.parseRangedJSONPointer("/foo/bar"));
  }

  @Test
  void testParseValueAtValid() {
    TreeValueAt at = TreeStackableValue.parseValueAt("@foo");
    Assertions.assertEquals("@foo", at.toString());
  }

  @Test
  void testParseValueAtInvalid() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parseValueAt("foo"));
  }

  @Test
  void testParsePathVariableValid() {
    TreeFullPath path = TreeStackableValue.parsePathVariable("abc");
    Assertions.assertEquals("abc", path.toString());
  }

  @Test
  void testParsePathVariableInvalid() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parsePathVariable("Abc"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parsePathVariable("ABC"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TreeStackableValue.parsePathVariable("abcD"));
  }
}
