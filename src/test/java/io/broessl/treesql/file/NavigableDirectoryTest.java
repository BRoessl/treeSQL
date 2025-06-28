package io.broessl.treesql.file;

import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreePrimitive;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NavigableDirectoryTest {

  public static final File TEST_DIR = new File("src/test/resources/root");

  @Test
  void test() {
    NavigableDirectory squealableFileSystem = new NavigableDirectory(TEST_DIR.toPath());
    Assertions.assertTrue(squealableFileSystem.getParentNode().isEmpty());
    Assertions.assertTrue(squealableFileSystem.getChildNode("nope").isEmpty());
    Assertions.assertTrue(squealableFileSystem.getChildNode("subFolder").isPresent());
    Assertions.assertTrue(squealableFileSystem.getChildNode("testA.json").isPresent());
    List<String> list =
        squealableFileSystem
            .getChildNode("subFolder")
            .get()
            .children()
            .map(tn -> tn.getSelfName().nativeValue().toString())
            .toList();
    Assertions.assertEquals(6, list.size());
    Assertions.assertTrue(list.contains("sub~SubFolder"));
    Assertions.assertTrue(list.contains("1.json"));
    Assertions.assertTrue(list.contains("2.json"));
    Assertions.assertTrue(list.contains("3.json"));
    Assertions.assertTrue(list.contains("4.json"));
    Assertions.assertTrue(list.contains("5.json"));
  }

  @Test
  void testList() {
    NavigableDirectory squealableFileSystem = new NavigableDirectory(TEST_DIR.toPath());
    Assertions.assertTrue(squealableFileSystem.getParentNode().isEmpty());
    Assertions.assertTrue(squealableFileSystem.getChildNode("nope").isEmpty());
    Assertions.assertTrue(squealableFileSystem.getChildNode("subFolder").isPresent());
    Assertions.assertTrue(squealableFileSystem.getChildNode("testA.json").isPresent());
    TreePrimitive tPrim = squealableFileSystem.getValue();
    TreeList tList = Assertions.assertInstanceOf(TreeList.class, tPrim);
    List<Object> nativeValues = tList.nativeValue();
    Assertions.assertTrue(nativeValues.contains("subFolder"));
    Assertions.assertTrue(nativeValues.contains("testA.json"));
  }
}
