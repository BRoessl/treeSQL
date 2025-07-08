package io.broessl.treesql.core;

import io.broessl.testutils.TestWithJsonData;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.json.NavigableJsonNode;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScannableTreeNodeTest extends TestWithJsonData {

  @Test
  void testSimpleScanNoMatch() {
    ScannableTreeNode node =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    List<ScannableTreeNode> scanResult = node.scan("/no_such_thing").toList();
    Assertions.assertEquals(0, scanResult.size());
  }

  @Test
  void testScanOutOfBound() {
    ScannableTreeNode node =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    List<ScannableTreeNode> scanResult = node.scan("/..~").toList();
    Assertions.assertEquals(0, scanResult.size());
  }

  @Test
  void testScanOutOfBoundComplex() {
    ScannableTreeNode node =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    List<ScannableTreeNode> scanResult = node.scan("/highly/..~/..~/..~").toList();
    Assertions.assertEquals(0, scanResult.size());
  }

  @Test
  void testSimpleScanDeepMatch() {
    ScannableTreeNode node =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    List<ScannableTreeNode> scanResult = node.scan("/highly/nested").toList();
    Assertions.assertEquals(1, scanResult.size());
    Assertions.assertEquals("{\"objects\":true}", scanResult.get(0).toString());
    Assertions.assertEquals("/highly/nested", scanResult.get(0).getContext().getEvaluationPath());
    Assertions.assertEquals("nested", scanResult.get(0).getName().getValue());
  }

  @Test
  void testSimpleRootMatch() {
    ScannableTreeNode node =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    List<ScannableTreeNode> scanResult = node.scan("").toList();
    Assertions.assertEquals(1, scanResult.size());
    Assertions.assertEquals(
        "{\"foo\":[\"bar\",\"baz\"],\"highly\":{\"nested\":{\"objects\":true}}}",
        scanResult.get(0).toString());
  }

  @Test
  void testSimpleScanArray() {
    ScannableTreeNode node =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataArray()));
    List<ScannableTreeNode> scanResult = node.scan("/0/A").toList();
    Assertions.assertEquals(1, scanResult.size());
    Assertions.assertEquals("1", scanResult.get(0).toString());
  }

  @Test
  void testSimpleMulitSteps() {
    ScannableTreeNode node =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    List<ScannableTreeNode> scanResult = node.scan("/{0,2}~").toList();
    Assertions.assertEquals(
        "{\"foo\":[\"bar\",\"baz\"],\"highly\":{\"nested\":{\"objects\":true}}}",
        scanResult.get(0).toString());
    Assertions.assertEquals("[\"bar\",\"baz\"]", scanResult.get(1).toString());
    Assertions.assertEquals("{\"nested\":{\"objects\":true}}", scanResult.get(2).toString());
    Assertions.assertEquals("\"bar\"", scanResult.get(3).toString());
    Assertions.assertEquals("\"baz\"", scanResult.get(4).toString());
    Assertions.assertEquals("{\"objects\":true}", scanResult.get(5).toString());
    Assertions.assertEquals("", scanResult.get(0).getContext().getEvaluationPath());
    Assertions.assertEquals("/foo", scanResult.get(1).getContext().getEvaluationPath());
    Assertions.assertEquals("/highly", scanResult.get(2).getContext().getEvaluationPath());
    Assertions.assertEquals("/foo/0", scanResult.get(3).getContext().getEvaluationPath());
    Assertions.assertEquals("/foo/1", scanResult.get(4).getContext().getEvaluationPath());
    Assertions.assertEquals("/highly/nested", scanResult.get(5).getContext().getEvaluationPath());
  }

  @Test
  void testSimpleScanAfterScan() {
    ScannableTreeNode firstScan =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    ;
    ScannableTreeNode scanResult = firstScan.scan("/highly").findFirst().get();
    ScannableTreeNode secondScan = scanResult.scan("/nested").findFirst().get();
    Assertions.assertEquals("{\"objects\":true}", secondScan.toString());
  }

  @Test
  void testSimpleBackScanAfterScan() {
    ScannableTreeNode firstScan =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    ScannableTreeNode scanResult = firstScan.scan("/highly/nested/objects").findFirst().get();
    ScannableTreeNode secondScan = scanResult.scan("/..~").findFirst().get();
    Assertions.assertEquals("{\"objects\":true}", secondScan.toString());
    Assertions.assertEquals(
        "/highly/nested/objects/~..", secondScan.getContext().getEvaluationPath());
    Assertions.assertEquals("/highly/nested", secondScan.absolutePath());
  }

  @Test
  void testSimpleSideScan() {
    ScannableTreeNode firstScan =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataArrayWithTenIntegers()));
    ScannableTreeNode scanResult = firstScan.scan("/5/[-1]~").findFirst().get();
    Assertions.assertEquals("4", scanResult.toString());
  }

  @Test
  void testSimpleSideScanIntermediate() {
    ScannableTreeNode firstScan =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataArray()));
    ScannableTreeNode scanResult = firstScan.scan("/1/[-1]~/A").findFirst().get();
    Assertions.assertEquals("1", scanResult.toString());
    Assertions.assertEquals("/1/[-1]~/A", scanResult.getContext().getEvaluationPath());
  }

  @Test
  void testUpToScan() {
    ScannableTreeNode firstScan =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataArrayWithTenIntegers()));
    Object[] scanResult = firstScan.scan("/5/[-99,-1]~").map(Object::toString).toArray();
    Assertions.assertArrayEquals(new String[] {"0", "1", "2", "3", "4"}, scanResult);
  }

  @Test
  void testUpToScanWithRangeLiteral() {
    ScannableTreeNode firstScan =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataArrayWithTenIntegers()));
    List<ScannableTreeNode> scanResult = firstScan.scan("/5/[-99,-1]~foo").toList();
    Assertions.assertEquals(5, scanResult.size());
    Assertions.assertEquals("[-5]~", scanResult.get(0).getContext().getBinding("foo").toString());
    Assertions.assertEquals("[-1]~", scanResult.get(4).getContext().getBinding("foo").toString());
  }

  @Test
  void testReversedScan() {
    ScannableTreeNode firstScan =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataArrayWithTenIntegers()));
    Object[] scanResult = firstScan.scan("/5/[+9999,+1]~").map(Object::toString).toArray();
    Assertions.assertArrayEquals(new String[] {"9", "8", "7", "6"}, scanResult);
  }

  @Test
  void testBindings() {
    ScannableTreeNode patternScan =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    List<ScannableTreeNode> scanResult = patternScan.scan("/~level_1/~level_2/~level_3").toList();
    Assertions.assertEquals(1, scanResult.size());
    Assertions.assertEquals("true", scanResult.get(0).toString());
    Assertions.assertEquals("/highly", scanResult.get(0).getContext().getBinding("~level_1"));
    Assertions.assertEquals(
        "/highly/nested", scanResult.get(0).getContext().getBinding("~level_2"));
    Assertions.assertEquals(
        "/highly/nested/objects", scanResult.get(0).getContext().getBinding("~level_3"));
    Assertions.assertEquals(
        new TreeString("highly"), scanResult.get(0).getContext().getBinding("level_1"));
    Assertions.assertEquals(
        new TreeString("nested"), scanResult.get(0).getContext().getBinding("level_2"));
    Assertions.assertEquals(
        new TreeString("objects"), scanResult.get(0).getContext().getBinding("level_3"));
    NavigableTreeNode ntn =
        Assertions.assertInstanceOf(
            NavigableTreeNode.class, scanResult.get(0).getContext().getBinding("@level_3"));
    Assertions.assertEquals(true, ntn.getValue().getValue());
  }

  @Test
  void testRegexWithBindings() {
    ScannableTreeNode patternScan =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    List<ScannableTreeNode> scanResult = patternScan.scan("/(foo)~level_1/~level_2").toList();
    Assertions.assertEquals(2, scanResult.size());
    Assertions.assertEquals(
        "/foo", scanResult.get(0).getContext().getBinding("~level_1").toString());
    Assertions.assertEquals(
        "/foo", scanResult.get(1).getContext().getBinding("~level_1").toString());
    Assertions.assertEquals("0", scanResult.get(0).getContext().getBinding("level_2").toString());
    Assertions.assertEquals("1", scanResult.get(1).getContext().getBinding("level_2").toString());
  }

  @Test
  void testBindingsBack() {
    ScannableTreeNode patternScan =
        ScannableTreeNode.forRoot(NavigableJsonNode.linkRoot(testDataSimpleDataTree()));
    List<ScannableTreeNode> scanResult =
        patternScan.scan("/~level_1/~level_2/~level_3/..~back_to_level_2").toList();
    Assertions.assertEquals(1, scanResult.size());
    Assertions.assertEquals("{\"objects\":true}", scanResult.get(0).toString());
    Assertions.assertEquals("/highly", scanResult.get(0).getContext().getBinding("~level_1"));
    Assertions.assertEquals(
        "/highly/nested", scanResult.get(0).getContext().getBinding("~level_2"));
    Assertions.assertEquals(
        "/highly/nested/objects", scanResult.get(0).getContext().getBinding("~level_3"));
    Assertions.assertEquals(
        "/highly/nested/objects/~..",
        scanResult.get(0).getContext().getBinding("~back_to_level_2"));
    Assertions.assertEquals(
        scanResult.get(0).getContext().getBinding("@back_to_level_2"),
        scanResult.get(0).getContext().getBinding("@level_2"));
  }
}
