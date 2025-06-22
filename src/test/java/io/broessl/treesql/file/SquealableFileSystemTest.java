package io.broessl.treesql.file;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SquealableFileSystemTest {

	public static final File TEST_DIR = new File("src/test/resources/root");

	@Test
	void test() {
		NavigableDirectory squealableFileSystem = new NavigableDirectory(TEST_DIR.toPath());
		Assertions.assertTrue(squealableFileSystem.getPreviousNode().isEmpty());
		Assertions.assertTrue(squealableFileSystem.getNextNode("nope").isEmpty());
		Assertions.assertTrue(squealableFileSystem.getNextNode("subFolder").isPresent());
		Assertions.assertTrue(squealableFileSystem.getNextNode("testA.json").isPresent());
		List<String> list = squealableFileSystem.getNextNode("subFolder").get().getAllNextNodes()
				.map(tn -> tn.getNameOrIndex().nativeValue().toString()).toList();
		Assertions.assertEquals(6, list.size());
		Assertions.assertTrue(list.contains("sub~SubFolder"));
		Assertions.assertTrue(list.contains("1.json"));
		Assertions.assertTrue(list.contains("2.json"));
		Assertions.assertTrue(list.contains("3.json"));
		Assertions.assertTrue(list.contains("4.json"));
		Assertions.assertTrue(list.contains("5.json"));

		Assertions.assertEquals("foo",
				squealableFileSystem.getNextNode("testA.json").get().getNextNode("#json").get().getNextNode("0").get().getValue().nativeValue());
	}

}
