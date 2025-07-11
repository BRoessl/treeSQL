package io.broessl.treesql.file;

import io.broessl.treesql.core.ScannableTreeNode;
import io.broessl.treesql.core.types.TreeNumber;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntegrationTests {

  @Test
  void testFileScan() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    List<String> scanResult =
        fileScan.scan("/~{0,3}/~my_json_root?JSON/0").map(t -> t.absolutePath()).toList();
    Assertions.assertEquals(7, scanResult.size());
    Assertions.assertTrue(scanResult.contains("/testA.json/my_json_root/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/1.json/my_json_root/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/2.json/my_json_root/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/3.json/my_json_root/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/4.json/my_json_root/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/5.json/my_json_root/0"));
    Assertions.assertTrue(
        scanResult.contains("/subFolder/sub~0SubFolder/testA.json/my_json_root/0"));
  }

  @Test
  void testFileScanIntoAndOutJson() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode into = fileScan.scan("/testA.json/~my_json?JSON/0").findFirst().orElseThrow();
    Assertions.assertEquals("0", into.getName().getValue().toString());
    ScannableTreeNode out = into.scan("/~../~..").findFirst().orElseThrow();
    Assertions.assertEquals("testA.json", out.getName().getValue().toString());
  }

  @Test
  void testFileScanWithTiled() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan
            .scan("/subFolder/sub~0SubFolder/testA.json/~my_json_root?JSON/0")
            .findFirst()
            .get();
    Assertions.assertEquals(
        "/subFolder/sub~0SubFolder/testA.json/my_json_root/0", scanResult.absolutePath());
  }

  @Test
  void testTextLines() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan.scan("/subFolder/lines.txt/~my_lines?LINES/0").findFirst().get();
    Assertions.assertEquals("/subFolder/lines.txt/my_lines/0", scanResult.absolutePath());
    Assertions.assertEquals("a,b,c", scanResult.getNavigableTreeNode().getValue().toString());
  }

  @Test
  void testRegexFilter() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan.scan("/subFolder/match.txt/~my_regex?REGEX(p.*)").findFirst().get();
    Assertions.assertEquals("/subFolder/match.txt/my_regex", scanResult.absolutePath());
    Assertions.assertEquals(
        "peter plays football in paris", scanResult.getNavigableTreeNode().getValue().toString());
  }

  @Test
  void testRegexUnnamed() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan
            .scan("/subFolder/match.txt/~my_regex?REGEX((.*) plays (.*) in (.*))/1")
            .findFirst()
            .get();
    Assertions.assertEquals("/subFolder/match.txt/my_regex/1", scanResult.absolutePath());
    Assertions.assertEquals("football", scanResult.getNavigableTreeNode().getValue().toString());
  }

  @Test
  void testRegexNamed() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan
            .scan(
                "/subFolder/match.txt/~my_regex?REGEX((?<who>.*) plays (?<what>.*) in (?<where>.*))/where")
            .findFirst()
            .get();
    Assertions.assertEquals("/subFolder/match.txt/my_regex/where", scanResult.absolutePath());
    Assertions.assertEquals("paris", scanResult.getNavigableTreeNode().getValue().toString());
  }

  @Test
  void testTextLinesWithRegex() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan
            .scan(
                "/subFolder/lines.txt/~my_lines?LINES/~idx_who_match/~my_regex?REGEX((?<who>.*) plays (?<what>.*) in (?<where>.*))/where")
            .findFirst()
            .get();
    Assertions.assertEquals("paris", scanResult.getNavigableTreeNode().getValue().toString());
    var num =
        Assertions.assertInstanceOf(
            TreeNumber.class, scanResult.getContext().getObject("idx_who_match"));
    Assertions.assertEquals(2, num.getValue().intValue());
  }

  @Test
  void testCsv() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan.scan("/subFolder/csv.csv/~my_csv?CSV/1/bar").findFirst().get();
    Assertions.assertEquals("/subFolder/csv.csv/my_csv/1/bar", scanResult.absolutePath());
    Assertions.assertEquals("4", scanResult.getNavigableTreeNode().getValue().toString());
  }

  @Test
  void testCsvA() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan
            .scan("/subFolder/csv_special_A.csv/~my_csv?CSV(delimiter=;&quote='&has=a)/1/foo")
            .findFirst()
            .get();
    Assertions.assertEquals("/subFolder/csv_special_A.csv/my_csv/1/foo", scanResult.absolutePath());
    Assertions.assertEquals("a3", scanResult.getNavigableTreeNode().getValue().toString());
  }

  @Test
  void testCsvANoMatch() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    Optional<ScannableTreeNode> scanResult =
        fileScan
            .scan("/subFolder/csv_special_A.csv/~my_csv?CSV(delimiter=;\nquote='\nhas=b)/1/foo")
            .findFirst();
    Assertions.assertTrue(scanResult.isEmpty());
  }

  @Test
  void testXmlBasicNavigation() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan
            .scan("/subFolder/catalog.xml/~catalog?XML(catalog)/product/0/@id")
            .findFirst()
            .get();
    Assertions.assertEquals(
        "/subFolder/catalog.xml/catalog/product/0/@id", scanResult.absolutePath());
    Assertions.assertEquals("p001", scanResult.getNavigableTreeNode().getValue().toString());
  }

  @Test
  void testXmlNestedElementNavigation() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan
            .scan(
                "/subFolder/catalog.xml/~catalog?XML/product/0/specifications/0/memory/0/ram/0/text()")
            .findFirst()
            .get();
    Assertions.assertEquals(
        "/subFolder/catalog.xml/catalog/product/0/specifications/0/memory/0/ram/0/text()",
        scanResult.absolutePath());
    Assertions.assertEquals("8GB", scanResult.getNavigableTreeNode().getValue().toString());
  }

  @Test
  void testXmlMultiResults() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    List<ScannableTreeNode> scanResult =
        fileScan
            .scan("/subFolder/catalog.xml/~catalog?XML/product/~/reviews/~/review/~/text()")
            .toList();
    Assertions.assertEquals(
        "/subFolder/catalog.xml/catalog/product/0/reviews/0/review/0/text()",
        scanResult.get(0).absolutePath());
    Assertions.assertEquals(
        "Excellent phone, great battery life!",
        scanResult.get(0).getNavigableTreeNode().getValue().toString());
    Assertions.assertEquals(
        "Good performance but expensive",
        scanResult.get(1).getNavigableTreeNode().getValue().toString());
  }
}
