package io.broessl.treesql.core;

import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.json.NavigableJsonNode;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class DirectiveRegistry {

  public static DirectiveRegistry instance() {
    return INSTANCE;
  }

  public static final DirectiveRegistry INSTANCE = new DirectiveRegistry();

  private DirectiveRegistry() {
    registerDirective(
        "~JSON",
        new Function<NavigableTreeNode, NavigableTreeNode>() {
          @Override
          public NavigableTreeNode apply(NavigableTreeNode node) {
            if (node.getValue() instanceof TreeString str) {
              return NavigableJsonNode.fromContent(str.nativeValue(), node, "~JSON");
            }
            return null;
          }
        });
  }

  Map<String, Function<NavigableTreeNode, NavigableTreeNode>> directives = new HashMap<>();

  public void registerDirective(
      String name, Function<NavigableTreeNode, NavigableTreeNode> directive) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Directive name cannot be null or empty");
    }
    if (!name.matches("~[A-Z]+")) {
      throw new IllegalArgumentException(
          "Directive name must be named with ~ followed by uppercase letters only");
    }
    if (directive == null) {
      throw new IllegalArgumentException("Directive cannot be null");
    }
    directives.put(name, directive);
  }

  public Function<NavigableTreeNode, NavigableTreeNode> getDirective(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Directive name cannot be null or empty");
    }
    Function<NavigableTreeNode, NavigableTreeNode> directive = directives.get(name);
    if (directive == null) {
      throw new NoSuchElementException("No directive found with name: " + name);
    }
    return directive;
  }
}
