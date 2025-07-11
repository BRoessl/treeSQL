package io.broessl.treesql.core.types;

import io.broessl.treesql.core.ScannableTreeNode;
import java.util.List;
import java.util.regex.Pattern;

public final class TreeRangedJSONPointer extends TreeContextValue {

  private final String selection;

  public TreeRangedJSONPointer(String pointer) {
    this.selection = pointer;
  }

  @Override
  public String toString() {
    return selection;
  }

  @Override
  public TreeValue getPrimitiveValue(ScannableTreeNode stn) {
    if (this.isContextAware()) {
      return this.contextAware(stn).getPrimitiveValue(stn);
    }
    // simple regex check if it is not a common "non-ranged" JSONPointer
    boolean hasRange = this.selection.matches("~[^01]");
    stn.getNavigableTreeNode().getRoot();
    List<TreeValue> resultOfPointer =
        ScannableTreeNode.forRoot(stn.getNavigableTreeNode().getRoot())
            .scan(this.selection)
            .map(s -> s.getNavigableTreeNode().getValue())
            .toList();
    if (hasRange) {
      return new TreeList(resultOfPointer);
    } else if (resultOfPointer.size() > 1) {
      throw new IllegalStateException(
          "non-ranged JSONPointer " + this.selection + " has yielded more than one result.");
    } else if (resultOfPointer.size() == 1) {
      return resultOfPointer.get(0);
    }
    return TreeNull.INSTANCE;
  }

  public TreeRangedJSONPointer contextAware(ScannableTreeNode stn) {
    String pointer;
    if (isContextAware()) {
      String context;
      if (this.selection.contains("/")) {
        context = selection.substring(0, selection.indexOf("/"));
        String pathToContext = expectAsString(stn, context).getValue();
        pointer = pathToContext + selection.substring(selection.indexOf("/"));
        return new TreeRangedJSONPointer(pointer);
      } else {
        context = selection;
        String pathToContext = expectAsString(stn, context).getValue();
        pointer = pathToContext;
        return new TreeRangedJSONPointer(pointer);
      }
    } else {
      return this;
    }
  }

  private boolean isContextAware() {
    return this.selection.startsWith("~");
  }

  public static final Pattern rangeLiteralPattern = Pattern.compile("/~([a-z][a-z0-9_]*)");

  @Override
  public List<String> getUsedRangedLiterals() {
    if (this.isContextAware()) {
      return List.of(selection.substring(1, selection.indexOf("/")));
    }
    return List.of();
  }

  public List<String> getProvidedRangedLiterals() {
    return rangeLiteralPattern
        .matcher(this.selection)
        .results()
        .map(match -> match.group(1))
        .distinct()
        .toList();
  }
}
