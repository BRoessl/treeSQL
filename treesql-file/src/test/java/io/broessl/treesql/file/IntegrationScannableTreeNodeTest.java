package io.broessl.treesql;

import io.broessl.treesql.core.ScannableTreeNode;
import io.broessl.treesql.file.NavigableDirectory;
import io.broessl.treesql.file.NavigableDirectoryTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScannableTreeNodeTest {

  @Test
  void testFileScan() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    List<String> scanResult = fileScan.scan("/{0,3}~/~JSON/0").map(t -> t.absolutePath()).toList();
    Assertions.assertEquals(7, scanResult.size());
    Assertions.assertTrue(scanResult.contains("/testA.json/~0JSON/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/1.json/~0JSON/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/2.json/~0JSON/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/3.json/~0JSON/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/4.json/~0JSON/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/5.json/~0JSON/0"));
    Assertions.assertTrue(scanResult.contains("/subFolder/sub~0SubFolder/testA.json/~0JSON/0"));
  }

  @Test
  void testFileScanIntoAndOutJson() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode into = fileScan.scan("/testA.json/~JSON/0").findFirst().orElseThrow();
    Assertions.assertEquals("0", into.getNameOrIndex().nativeValue().toString());
    ScannableTreeNode out = into.scan("/..~/..~").findFirst().orElseThrow();
    Assertions.assertEquals("testA.json", out.getNameOrIndex().nativeValue().toString());
  }

  @Test
  void testFileScanWithTiled() {
    NavigableDirectory squealableFileSystem =
        new NavigableDirectory(NavigableDirectoryTest.TEST_DIR.toPath());
    ScannableTreeNode fileScan = ScannableTreeNode.forRoot(squealableFileSystem);
    ScannableTreeNode scanResult =
        fileScan.scan("/subFolder/sub~0SubFolder/testA.json/~JSON/0").findFirst().get();
    Assertions.assertEquals(
        "/subFolder/sub~0SubFolder/testA.json/~0JSON/0", scanResult.absolutePath());
  }
}
