package io.broessl.treesql.file;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class NavigableDirectory implements NavigableTreeNode {

  private Path path;

  private NavigableTreeNode parent;

  public NavigableDirectory(Path fileOrDirectory) {
    this(fileOrDirectory, null);
  }

  public NavigableDirectory(Path fileOrDirectory, NavigableTreeNode parent) {
    if (!Files.exists(fileOrDirectory, LinkOption.NOFOLLOW_LINKS)) {
      throw new IllegalArgumentException(
          "directory or file does not exist " + fileOrDirectory.toString());
    }
    this.path = fileOrDirectory;
    this.parent = parent;
  }

  @Override
  public TreeNodeIdentifier getName() {
    if (parent == null) {
      return null;
    }
    return new TreeString(path.getFileName().toString());
  }

  @Override
  public Optional<NavigableTreeNode> getParent() {
    return Optional.ofNullable(this.parent);
  }

  @Override
  public Optional<NavigableTreeNode> getChild(String nameOrIndex) {
    try {

      Path nextPath = this.path.resolve(nameOrIndex);
      if (Files.exists(nextPath, LinkOption.NOFOLLOW_LINKS)) {
        return Optional.of(new NavigableDirectory(nextPath, this));
      }
    } catch (Exception e) {
      // ignore
      // might get an exception for directives containing '?'
    }
    return Optional.empty();
  }

  @Override
  public Optional<NavigableTreeNode> getSibling(Integer indexOffset) {
    return Optional.empty();
  }

  @Override
  public Stream<NavigableTreeNode> children() {
    try {
      if (Files.isDirectory(this.path, LinkOption.NOFOLLOW_LINKS)) {
        return Files.list(path)
            .map(
                next -> {
                  return (NavigableTreeNode) new NavigableDirectory(next, this);
                });
      }
      return Stream.empty();
    } catch (Exception e) {
      return Stream.empty();
    }
  }

  @Override
  public Optional<Integer> getSize() {
    return Optional.empty();
  }

  @Override
  public boolean isListNode() {
    return false;
  }

  @Override
  public boolean isMapNode() {
    return true;
  }

  @Override
  public TreeValue getValue() {
    if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
      List<TreeValue> list = new ArrayList<TreeValue>();
      try {
        Files.list(path)
            .forEach(
                new Consumer<Path>() {
                  @Override
                  public void accept(Path t) {
                    list.add(new TreeString(t.getFileName().toString()));
                  }
                });
      } catch (IOException e) {
        return new TreeList(list);
      }
      return new TreeList(list);
    }
    if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
      try {
        return new TreeString(Files.readString(path));
      } catch (IOException e) {
        return TreeNull.INSTANCE;
      }
    }
    return TreeNull.INSTANCE;
  }
}
