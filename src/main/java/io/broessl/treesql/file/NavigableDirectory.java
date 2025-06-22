package io.broessl.treesql.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.json.NavigableJsonNode;

public class NavigableDirectory implements NavigableTreeNode {

	private static final String JSON_DIRECTIVE = "#json";

	private Path path;

	private NavigableTreeNode parent;

	public NavigableDirectory(Path fileOrDirectory) {
		this(fileOrDirectory, null);
	}

	public NavigableDirectory(Path fileOrDirectory, NavigableTreeNode parent) {
		if (!Files.exists(fileOrDirectory, LinkOption.NOFOLLOW_LINKS)) {
			throw new IllegalArgumentException("directory or file does not exist " + fileOrDirectory.toString());
		}
		this.path = fileOrDirectory;
		this.parent = parent;
	}

	@Override
	public TreeNodeIdentifier getNameOrIndex() {
		if (parent == null) {
			return new TreeString("");
		}
		return new TreeString(path.getFileName().toString());
	}

	@Override
	public Optional<NavigableTreeNode> getPreviousNode() {
		return Optional.ofNullable(this.parent);
	}

	@Override
	public Optional<NavigableTreeNode> getNextNode(String nameOrIndex) {
		if (Files.isRegularFile(this.path, LinkOption.NOFOLLOW_LINKS)) {
			try {
				if (JSON_DIRECTIVE.equals(nameOrIndex)) {
					byte[] allBytes = Files.readAllBytes(this.path);
					NavigableJsonNode fromContent = NavigableJsonNode.fromContent(allBytes, this, JSON_DIRECTIVE);
					return Optional.ofNullable(fromContent);
				}
				return Optional.empty();
			} catch (IOException e) {
				return Optional.empty();
			}
		}
		Path nextPath = this.path.resolve(nameOrIndex);
		if (Files.exists(nextPath, LinkOption.NOFOLLOW_LINKS)) {
			return Optional.of(new NavigableDirectory(nextPath, this));
		}
		return Optional.empty();
	}

	@Override
	public Optional<NavigableTreeNode> getSibling(Integer indexOffset) {
		return Optional.empty();
	}

	@Override
	public Stream<NavigableTreeNode> getAllNextNodes() {
		try {
			if (Files.isDirectory(this.path, LinkOption.NOFOLLOW_LINKS)) {
				return Files.list(path).map(next -> {
					return (NavigableTreeNode) new NavigableDirectory(next, this);
				});
			}
			if (Files.isRegularFile(this.path, LinkOption.NOFOLLOW_LINKS)) {
					String fileName = this.path.getFileName().toString();
					if (fileName.endsWith(".json")) {
						byte[] allBytes = Files.readAllBytes(this.path);
						NavigableJsonNode fromContent = NavigableJsonNode.fromContent(allBytes, this, JSON_DIRECTIVE);
						return Stream.ofNullable((NavigableTreeNode) fromContent);
					}
					return Stream.empty();
			}
			return Stream.empty();
		} catch (Exception e) {
			return Stream.empty();
		}
	}

	@Override
	public Optional<Integer> getSelfIndex() {
		return Optional.empty();
	}

	@Override
	public Optional<Integer> getSize() {
		return Optional.empty();
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public boolean isObject() {
		return true;
	}

	@Override
	public TreePrimitive getValue() {
		if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			return new TreeString("DIRECTORY_OBJECT");
		}
		if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
			return new TreeString("FILE_OBJECT");
		}
		return TreeNull.INSTANCE;
	}

}
