package io.broessl.treesql.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NavigableJsonNode implements NavigableTreeNode {

  String externalNamedRoot;

  private NavigableTreeNode parent;

  private JsonNode delegated;

  public JsonNode unlink() {
    return delegated;
  }

  public NavigableJsonNode(JsonNode delegated, NavigableTreeNode parent, String externalNamed) {
    this.delegated = delegated;
    this.parent = parent;
    this.externalNamedRoot = externalNamed;
  }

  public NavigableJsonNode(JsonNode delegated, NavigableJsonNode parent) {
    this(delegated, parent, null);
  }

  public static NavigableJsonNode fromContent(byte[] content, NavigableJsonNode parent) {
    JsonNode tree;
    try {
      tree = new ObjectMapper().readTree(content);
    } catch (Exception e) {
      return null;
    }
    return new NavigableJsonNode(tree, parent);
  }

  public static NavigableJsonNode fromContent(
      byte[] content, NavigableTreeNode parent, String externalNamed) {
    JsonNode tree;
    try {
      tree = new ObjectMapper().readTree(content);
    } catch (Exception e) {
      return null;
    }
    return new NavigableJsonNode(tree, parent, externalNamed);
  }

  public static NavigableJsonNode fromContent(
      String content, NavigableTreeNode parent, String externalNamed) {
    JsonNode tree;
    try {
      tree = new ObjectMapper().readTree(content);
    } catch (Exception e) {
      return null;
    }
    return new NavigableJsonNode(tree, parent, externalNamed);
  }

  public static NavigableJsonNode linkRoot(JsonNode rootNode) {
    return new NavigableJsonNode(rootNode, null);
  }

  public static Optional<NavigableJsonNode> tryLink(JsonNode rootNode, JsonNode descendant) {
    LinkedList<JsonNode> path = new LinkedList<>();
    if (rootNode.size() > 0) {
      path.add(rootNode);
      for (JsonNode child : rootNode) {
        path.add(child);
        if (find(path, child, descendant)) return Optional.of(buildFromList(path));
        path.removeLast();
      }
      return Optional.empty();
    }
    return Optional.empty();
  }

  private static boolean find(LinkedList<JsonNode> path, JsonNode current, JsonNode findMe) {
    if (current == findMe) {
      return true;
    }
    for (JsonNode nextLevel : current) {
      path.add(nextLevel);
      if (find(path, nextLevel, findMe)) {
        return true;
      }
      path.removeLast();
    }
    return false;
  }

  private static NavigableJsonNode buildFromList(LinkedList<JsonNode> path) {
    NavigableJsonNode currentLinked = null;
    for (JsonNode jsonNode : path) {
      NavigableJsonNode nextLinked = new NavigableJsonNode(jsonNode, currentLinked);
      currentLinked = nextLinked;
    }
    return currentLinked;
  }

  public boolean isRoot() {
    return parent == null;
  }

  @Override
  public TreeNodeIdentifier getSelfName() {
    if (parent == null) {
      return null;
    }
    // jackson api specific: a com.fasterxml.jackson.databind.JsonNode's name is
    // only accesible by
    // parent map/object or list/array
    if (parent instanceof NavigableJsonNode jparent) {
      if (parent.isListNode()) {
        return new TreeNumber(findIndexFor(delegated, ((NavigableJsonNode) parent).delegated));
      }
      if (!parent.isMapNode()) {
        throw new IllegalStateException("a parent node is neither an array nor an object node");
      }
      return new TreeString(findNameFor(delegated, jparent.delegated));
    }
    // it is the json root object but inside a non-json parent (e.g. a file) exists
    // we do not know the name, we are "embedded"
    return new TreeString(externalNamedRoot);
  }

  @Override
  public Optional<Integer> getSize() {
    if (delegated.isContainerNode()) {
      return Optional.of(delegated.size());
    }
    return Optional.empty();
  }

  private String findNameFor(JsonNode child, JsonNode container) {
    return container
        .propertyStream()
        .filter(e -> e.getValue() == child)
        .map(Entry::getKey)
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "path is not consistent, object referenced as parent does not contain child node. child might have been deleted from container."));
  }

  private Integer findIndexFor(JsonNode child, JsonNode container) {
    for (int i = 0; i < container.size(); i++) {
      JsonNode jsonNode = container.get(i);
      if (jsonNode == child) {
        return i;
      }
    }
    throw new IllegalStateException(
        "path is not consistent, array referenced as parent does not contain child node. child might have been deleted from container.");
  }

  @Override
  public Optional<NavigableTreeNode> getParentNode() {
    return Optional.ofNullable(parent);
  }

  @Override
  public Optional<NavigableTreeNode> getChildNode(String nameOrIndex) {
    if (!delegated.isContainerNode()) {
      return Optional.empty();
    }
    if (delegated.isArray()) {
      try {
        int idx = Integer.parseInt(nameOrIndex);
        JsonNode childNode = delegated.get(idx);
        if (childNode == null) {
          return Optional.empty();
        }
        return Optional.of(new NavigableJsonNode(childNode, this));
      } catch (Exception e) {
        return Optional.empty();
      }
    }
    if (delegated.isObject()) {
      JsonNode childNode = delegated.get(nameOrIndex);
      if (childNode == null) {
        return Optional.empty();
      }
      return Optional.of(new NavigableJsonNode(childNode, this));
    }
    return Optional.empty();
  }

  @Override
  public Optional<NavigableTreeNode> getSibling(Integer indexOffset) {
    if (parent == null || !parent.isListNode()) {
      // index manipulation is only applicable on arrays
      return Optional.empty();
    }
    int findIndexToManipulate = findIndexFor(delegated, ((NavigableJsonNode) parent).delegated);
    JsonNode sibling =
        ((NavigableJsonNode) parent).delegated.get(findIndexToManipulate + indexOffset);
    if (sibling == null) {
      // sibling is null if requested access is out of index
      return Optional.empty();
    }
    return Optional.of(new NavigableJsonNode(sibling, (NavigableJsonNode) parent));
  }

  @Override
  public Stream<NavigableTreeNode> children() {
    return StreamSupport.stream(delegated.spliterator(), false)
        .map(n -> new NavigableJsonNode(n, this));
  }

  @Override
  public boolean isListNode() {
    return delegated.isArray();
  }

  @Override
  public boolean isMapNode() {
    return delegated.isObject();
  }

  @Override
  public String toString() {
    return delegated.toString();
  }

  @Override
  public TreePrimitive getValue() {
    if (delegated == null || delegated.isNull() || delegated.isMissingNode()) {
      return TreeNull.INSTANCE;
    }
    if (delegated.isNumber()) {
      return new TreeNumber(delegated.numberValue());
    }
    if (delegated.isBoolean()) {
      return new TreeBool(delegated.booleanValue());
    }
    if (delegated.isObject()) {
      var internalList = new ArrayList<TreePrimitive>();
      delegated.fieldNames().forEachRemaining(s -> internalList.add(new TreeString(s)));
      return new TreeList(internalList);
    }
    if (delegated.isArray()) {
      List<TreePrimitive> list =
          IntStream.range(0, delegated.size())
              .mapToObj(i -> (TreePrimitive) new TreeNumber(i))
              .toList();
      return new TreeList(list);
    }
    if (delegated.isTextual()) {
      return new TreeString(delegated.textValue());
    }
    throw new IllegalStateException("case not implemented: " + delegated.getNodeType());
  }
}
