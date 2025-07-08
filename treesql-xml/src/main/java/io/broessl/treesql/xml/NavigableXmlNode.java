package io.broessl.treesql.xml;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.core.types.TreeNull;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class NavigableXmlNode implements NavigableTreeNode {

  Element element;
  Integer index;

  Attribute attribute;
  List<Element> nodes;
  String name;

  String textNode;

  NavigableTreeNode parent;

  public NavigableXmlNode(Attribute attribute, NavigableTreeNode parent, String name) {
    this.attribute = attribute;
    this.name = name;
    this.parent = parent;
  }

  public NavigableXmlNode(Element node, NavigableTreeNode parent, Integer index) {
    this.element = node;
    this.index = index;
    this.parent = parent;
  }

  public NavigableXmlNode(List<Element> nodes, NavigableTreeNode parent, String name) {
    this.nodes = nodes;
    this.name = name;
    this.parent = parent;
  }

  public NavigableXmlNode(String textNode, NavigableTreeNode parent) {
    this.textNode = textNode;
    this.parent = parent;
  }

  public NavigableXmlNode(Element root, NavigableTreeNode parentNode, String externalRootName) {
    this.element = root;
    this.name = externalRootName;
    this.parent = parentNode;
  }

  @Override
  public TreeNodeIdentifier getName() {
    if (textNode != null) {
      return new TreeString("text()");
    }
    if (attribute != null) {
      return new TreeString("@" + attribute.getName());
    }
    if (element instanceof Element elem) {
      if (elem.isRootElement()) {
        return name != null ? new TreeString(name) : null;
      } else {
        return new TreeNumber(this.index);
      }
    }
    if (nodes != null) {
      return new TreeString(name);
    }
    throw new IllegalStateException();
  }

  @Override
  public Optional<NavigableTreeNode> getParent() {
    return Optional.ofNullable(parent);
  }

  @Override
  public Optional<NavigableTreeNode> getChild(String nameOrIndex) {
    if (element instanceof Element elem) {
      if (nameOrIndex.startsWith("@")) {
        Attribute att = elem.attribute(nameOrIndex.substring(1));
        if (att != null) {
          return Optional.of(new NavigableXmlNode(att, this, nameOrIndex));
        }
        return Optional.empty();
      } else if ("text()".equals(nameOrIndex)) {
        String text = elem.getTextTrim();
        if (text != null && !text.isEmpty()) {
          return Optional.of(new NavigableXmlNode(text, this));
        } else {
          return Optional.empty();
        }
      } else {
        List<Element> elements = elem.elements(nameOrIndex);
        if (elements.size() == 0) {
          return Optional.empty();
        }
        return Optional.of(new NavigableXmlNode(elements, this, nameOrIndex));
      }
    } else if (nodes != null) {
      try {
        Integer idx = Integer.parseInt(nameOrIndex);
        if (nodes.size() <= idx || idx < 0) {
          return Optional.empty();
        }
        return Optional.of(new NavigableXmlNode(nodes.get(idx), this, idx));

      } catch (NumberFormatException e) {
        return Optional.empty();
      }
    }
    // e.g. Attribute/Text Nodes have no children
    return Optional.empty();
  }

  @Override
  public Optional<NavigableTreeNode> getSibling(Integer indexOffset) {
    if (element != null && !element.isRootElement()) {
      int siblingIndex = this.index + indexOffset;
      try {
        NavigableXmlNode parentNode = (NavigableXmlNode) getParent().orElseThrow();
        return Optional.of(
            new NavigableXmlNode(parentNode.nodes.get(siblingIndex), parentNode, siblingIndex));
      } catch (IndexOutOfBoundsException e) {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  @Override
  public Stream<NavigableTreeNode> children() {
    if (textNode != null || attribute != null) {
      return Stream.empty();
    }
    if (element != null) {
      List<NavigableTreeNode> childrenList = new ArrayList<>();
      element.elements().stream()
          .map(e -> e.getName())
          .distinct()
          .forEach(
              name -> {
                childrenList.add(new NavigableXmlNode(element.elements(name), this, name));
              });
      element.attributes().stream()
          .forEach(
              att -> {
                childrenList.add(new NavigableXmlNode(att, this, "@" + att.getName()));
              });
      if (element.getTextTrim() != null && !element.getTextTrim().isEmpty()) {
        childrenList.add(new NavigableXmlNode(element.getTextTrim(), this));
      }
      return childrenList.stream();
    }
    if (nodes != null) {
      List<NavigableTreeNode> childrenList = new ArrayList<>();

      for (int i = 0; i < nodes.size(); i++) {
        childrenList.add(new NavigableXmlNode(nodes.get(i), this, i));
      }
      return childrenList.stream();
    }
    throw new IllegalStateException("Code should be unreachable.");
  }

  @Override
  public Optional<Integer> getSize() {
    if (isListNode()) {
      return Optional.of(nodes.size());
    }
    if (isMapNode()) {
      return Optional.of(((TreeList) getValue()).size());
    }
    return Optional.empty();
  }

  @Override
  public boolean isListNode() {
    return nodes != null;
  }

  @Override
  public boolean isMapNode() {
    return element != null && !(attribute != null);
  }

  @Override
  public TreeValue getValue() {
    if (attribute != null) {
      return new TreeString(attribute.getValue());
    }
    if (textNode != null) {
      return new TreeString(textNode);
    }
    if (element instanceof Element elem) {
      List<TreeValue> childrenList = new ArrayList<>();
      elem.elements().stream()
          .map(e -> e.getName())
          .distinct()
          .map(str -> new TreeString(str))
          .forEach(childrenList::add);
      elem.attributes().stream()
          .map(a -> new TreeString("@" + a.getName()))
          .forEach(childrenList::add);
      if (elem.getTextTrim() != null && !elem.getTextTrim().isEmpty()) {
        childrenList.add(new TreeString("text()"));
      }
      return new TreeList(childrenList);
    }
    if (nodes != null) {
      List<TreeValue> childNames =
          IntStream.range(0, nodes.size())
              .mapToObj(i -> (TreeValue) new TreeNumber((Integer) i))
              .toList();
      return new TreeList(childNames);
    }
    // not an attribute, not a node list, no text content, what could it be?
    return TreeNull.INSTANCE;
  }
}
