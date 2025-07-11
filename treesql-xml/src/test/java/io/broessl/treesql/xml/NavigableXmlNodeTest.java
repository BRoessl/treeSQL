package io.broessl.treesql.xml;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NavigableXmlNodeTest {

  private Document document;
  private Element rootElement;
  private NavigableXmlNode rootNode;

  @BeforeEach
  void setUp() throws Exception {
    // Create a sample XML document for testing
    String xmlString =
        """
            <root xmlns:ns="http://example.com" id="123">
                <person name="John" age="30">
                    <address>
                        <street>Main St</street>
                        <city>New York</city>
                    </address>
                    <phone>555-1234</phone>
                </person>
                <person name="Jane" age="25">
                    <address>
                        <street>Oak Ave</street>
                        <city>Boston</city>
                    </address>
                </person>
                <metadata>Some metadata text</metadata>
                <empty></empty>
            </root>
            """;

    document = DocumentHelper.parseText(xmlString);
    rootElement = document.getRootElement();
    rootNode = new NavigableXmlNode(rootElement, null, "root");
  }

  @Test
  void testConstructorWithAttribute() {
    Element personElement = rootElement.element("person");
    NavigableXmlNode attributeNode =
        new NavigableXmlNode(personElement.attribute("name"), rootNode, "@name");

    assertEquals("@name", attributeNode.getName().getValue());
    assertEquals("John", attributeNode.getValue().getValue());
    assertTrue(attributeNode.getParent().isPresent());
    assertFalse(attributeNode.isListNode());
    assertFalse(attributeNode.isMapNode());
  }

  @Test
  void testConstructorWithElement() {
    Element personElement = rootElement.element("person");
    NavigableXmlNode elementNode = new NavigableXmlNode(personElement, rootNode, 0);

    assertEquals(0, ((TreeNumber) elementNode.getName()).getValue().intValue());
    assertTrue(elementNode.getParent().isPresent());
    assertFalse(elementNode.isListNode());
    assertTrue(elementNode.isMapNode());
  }

  @Test
  void testConstructorWithElementList() {
    List<Element> personElements = rootElement.elements("person");
    NavigableXmlNode listNode = new NavigableXmlNode(personElements, rootNode, "person");

    assertEquals("person", listNode.getName().getValue());
    assertTrue(listNode.isListNode());
    assertFalse(listNode.isMapNode());
    assertEquals(2, listNode.getSize().orElse(-1));
  }

  @Test
  void testConstructorWithTextNode() {
    NavigableXmlNode textNode = new NavigableXmlNode("Some text content", rootNode);

    assertEquals("text()", textNode.getName().getValue());
    assertEquals("Some text content", textNode.getValue().getValue());
    assertFalse(textNode.isListNode());
    assertFalse(textNode.isMapNode());
  }

  @Test
  void testConstructorWithExternalRootName() {
    NavigableXmlNode namedRootNode = new NavigableXmlNode(rootElement, null, "customRoot");

    assertEquals("customRoot", namedRootNode.getName().getValue());
    assertTrue(namedRootNode.getParent().isEmpty());
  }

  @Test
  void testGetSelfNameForRootElement() {
    // Root element without external name
    NavigableXmlNode rootWithoutName =
        new NavigableXmlNode(rootElement, (NavigableTreeNode) null, (String) null);
    assertNull(rootWithoutName.getName());

    // Root element with external name
    assertEquals("root", rootNode.getName().getValue());
  }

  @Test
  void testGetSelfNameForTextNode() {
    NavigableXmlNode textNode = new NavigableXmlNode("test text", rootNode);
    assertEquals("text()", textNode.getName().getValue());
  }

  @Test
  void testGetParentNode() {
    assertTrue(rootNode.getParent().isEmpty());

    Element personElement = rootElement.element("person");
    NavigableXmlNode personNode = new NavigableXmlNode(personElement, rootNode, 0);
    assertTrue(personNode.getParent().isPresent());
    assertEquals(rootNode, personNode.getParent().get());
  }

  @Test
  void testGetChildNodeByAttribute() {
    Optional<NavigableTreeNode> child = rootNode.getChild("@id");
    assertTrue(child.isPresent());
    assertEquals("123", child.get().getValue().getValue());
  }

  @Test
  void testGetChildNodeByAttributeNotFound() {
    Optional<NavigableTreeNode> child = rootNode.getChild("@nonexistent");
    assertTrue(child.isEmpty());
  }

  @Test
  void testGetChildNodeByTextContent() {
    Element metadataElement = rootElement.element("metadata");
    NavigableXmlNode metadataNode = new NavigableXmlNode(metadataElement, rootNode, 0);

    Optional<NavigableTreeNode> textChild = metadataNode.getChild("text()");
    assertTrue(textChild.isPresent());
    assertEquals("Some metadata text", textChild.get().getValue().getValue());
  }

  @Test
  void testGetChildNodeByTextContentEmpty() {
    Element emptyElement = rootElement.element("empty");
    NavigableXmlNode emptyNode = new NavigableXmlNode(emptyElement, rootNode, 0);

    Optional<NavigableTreeNode> textChild = emptyNode.getChild("text()");
    assertTrue(textChild.isEmpty());
  }

  @Test
  void testGetChildNodeByElementName() {
    Optional<NavigableTreeNode> child = rootNode.getChild("person");
    assertTrue(child.isPresent());
    assertTrue(child.get().isListNode());
    assertEquals(2, child.get().getSize().orElse(-1));
  }

  @Test
  void testGetChildNodeByElementNameNotFound() {
    Optional<NavigableTreeNode> child = rootNode.getChild("nonexistent");
    assertTrue(child.isEmpty());
  }

  @Test
  void testGetChildNodeFromListByIndex() {
    List<Element> personElements = rootElement.elements("person");
    NavigableXmlNode listNode = new NavigableXmlNode(personElements, rootNode, "person");

    Optional<NavigableTreeNode> firstPerson = listNode.getChild("0");
    assertTrue(firstPerson.isPresent());

    Optional<NavigableTreeNode> secondPerson = listNode.getChild("1");
    assertTrue(secondPerson.isPresent());

    Optional<NavigableTreeNode> outOfBounds = listNode.getChild("2");
    assertTrue(outOfBounds.isEmpty());

    Optional<NavigableTreeNode> negative = listNode.getChild("-1");
    assertTrue(negative.isEmpty());

    Optional<NavigableTreeNode> invalidIndex = listNode.getChild("abc");
    assertTrue(invalidIndex.isEmpty());
  }

  @Test
  void testGetChildNodeFromAttributeOrTextNode() {
    // Attribute nodes should have no children
    Element personElement = rootElement.element("person");
    NavigableXmlNode attributeNode =
        new NavigableXmlNode(personElement.attribute("name"), rootNode, "@name");

    Optional<NavigableTreeNode> child = attributeNode.getChild("anything");
    assertTrue(child.isEmpty());

    // Text nodes should have no children
    NavigableXmlNode textNode = new NavigableXmlNode("test text", rootNode);
    Optional<NavigableTreeNode> textChild = textNode.getChild("anything");
    assertTrue(textChild.isEmpty());
  }

  @Test
  void testGetSibling() {
    List<Element> personElements = rootElement.elements("person");
    NavigableXmlNode listNode = new NavigableXmlNode(personElements, rootNode, "person");
    NavigableXmlNode firstPerson = new NavigableXmlNode(personElements.get(0), listNode, 0);

    // Test getting next sibling
    Optional<NavigableTreeNode> nextSibling = firstPerson.getSibling(1);
    assertTrue(nextSibling.isPresent());

    // Test getting previous sibling (should be empty for first element)
    Optional<NavigableTreeNode> prevSibling = firstPerson.getSibling(-1);
    assertTrue(prevSibling.isEmpty());

    // Test out of bounds
    Optional<NavigableTreeNode> outOfBounds = firstPerson.getSibling(10);
    assertTrue(outOfBounds.isEmpty());
  }

  @Test
  void testGetSiblingForRootElement() {
    // Root elements should not have siblings
    Optional<NavigableTreeNode> sibling = rootNode.getSibling(1);
    assertTrue(sibling.isEmpty());
  }

  @Test
  void testChildren() {
    List<NavigableTreeNode> children = rootNode.children().collect(Collectors.toList());

    // Should include: person (element), metadata (element), empty (element), @id (attribute)
    assertTrue(children.size() >= 4);

    // Check that we have the expected child types
    boolean hasPersonElement =
        children.stream().anyMatch(child -> "person".equals(child.getName().getValue()));
    boolean hasIdAttribute =
        children.stream().anyMatch(child -> "@id".equals(child.getName().getValue()));

    assertTrue(hasPersonElement);
    assertTrue(hasIdAttribute);
  }

  @Test
  void testChildrenForAttributeNode() {
    Element personElement = rootElement.element("person");
    NavigableXmlNode attributeNode =
        new NavigableXmlNode(personElement.attribute("name"), rootNode, "@name");

    List<NavigableTreeNode> children = attributeNode.children().collect(Collectors.toList());
    assertTrue(children.isEmpty());
  }

  @Test
  void testChildrenForTextNode() {
    NavigableXmlNode textNode = new NavigableXmlNode("test text", rootNode);
    List<NavigableTreeNode> children = textNode.children().collect(Collectors.toList());
    assertTrue(children.isEmpty());
  }

  @Test
  void testChildrenForListNode() {
    List<Element> personElements = rootElement.elements("person");
    NavigableXmlNode listNode = new NavigableXmlNode(personElements, rootNode, "person");

    List<NavigableTreeNode> children = listNode.children().collect(Collectors.toList());
    assertEquals(2, children.size());

    // Children should be indexed 0, 1
    assertEquals(0, ((TreeNumber) children.get(0).getName()).getValue().intValue());
    assertEquals(1, ((TreeNumber) children.get(1).getName()).getValue().intValue());
  }

  @Test
  void testGetSize() {
    // List node size
    List<Element> personElements = rootElement.elements("person");
    NavigableXmlNode listNode = new NavigableXmlNode(personElements, rootNode, "person");
    assertEquals(2, listNode.getSize().orElse(-1));

    // Map node size (element with children)
    TreeList value = (TreeList) rootNode.getValue();
    assertEquals(value.size(), rootNode.getSize().orElse(-1));

    // Attribute node (should be empty)
    Element personElement = rootElement.element("person");
    NavigableXmlNode attributeNode =
        new NavigableXmlNode(personElement.attribute("name"), rootNode, "@name");
    assertTrue(attributeNode.getSize().isEmpty());
  }

  @Test
  void testIsListNode() {
    // List node
    List<Element> personElements = rootElement.elements("person");
    NavigableXmlNode listNode = new NavigableXmlNode(personElements, rootNode, "person");
    assertTrue(listNode.isListNode());

    // Element node
    assertFalse(rootNode.isListNode());

    // Attribute node
    Element personElement = rootElement.element("person");
    NavigableXmlNode attributeNode =
        new NavigableXmlNode(personElement.attribute("name"), rootNode, "@name");
    assertFalse(attributeNode.isListNode());
  }

  @Test
  void testIsMapNode() {
    // Element node
    assertTrue(rootNode.isMapNode());

    // List node
    List<Element> personElements = rootElement.elements("person");
    NavigableXmlNode listNode = new NavigableXmlNode(personElements, rootNode, "person");
    assertFalse(listNode.isMapNode());

    // Attribute node
    Element personElement = rootElement.element("person");
    NavigableXmlNode attributeNode =
        new NavigableXmlNode(personElement.attribute("name"), rootNode, "@name");
    assertFalse(attributeNode.isMapNode());
  }

  @Test
  void testGetValueForAttribute() {
    Element personElement = rootElement.element("person");
    NavigableXmlNode attributeNode =
        new NavigableXmlNode(personElement.attribute("name"), rootNode, "@name");

    TreeValue value = attributeNode.getValue();
    assertTrue(value instanceof TreeString);
    assertEquals("John", value.getValue());
  }

  @Test
  void testGetValueForTextNode() {
    NavigableXmlNode textNode = new NavigableXmlNode("test content", rootNode);

    TreeValue value = textNode.getValue();
    assertTrue(value instanceof TreeString);
    assertEquals("test content", value.getValue());
  }

  @Test
  void testGetValueForElementNode() {
    TreeValue value = rootNode.getValue();
    assertTrue(value instanceof TreeList);

    TreeList list = (TreeList) value;
    assertTrue(list.size() > 0);

    // Should contain child element names and attribute names
    List<String> childNames =
        list.stream().map(item -> item.getValue().toString()).collect(Collectors.toList());

    assertTrue(childNames.contains("person"));
    assertTrue(childNames.contains("metadata"));
    assertTrue(childNames.contains("@id"));
  }

  @Test
  void testGetValueForListNode() {
    List<Element> personElements = rootElement.elements("person");
    NavigableXmlNode listNode = new NavigableXmlNode(personElements, rootNode, "person");

    TreeValue value = listNode.getValue();
    assertTrue(value instanceof TreeList);

    TreeList list = (TreeList) value;
    assertEquals(2, list.size());

    // Should contain indices 0, 1
    assertEquals(0, ((TreeNumber) list.get(0)).getValue().intValue());
    assertEquals(1, ((TreeNumber) list.get(1)).getValue().intValue());
  }

  @Test
  void testAbsolutePath() {
    // Test root path
    assertEquals("", rootNode.absolutePath());

    // Test child element path
    Optional<NavigableTreeNode> personList = rootNode.getChild("person");
    assertTrue(personList.isPresent());
    assertEquals("/person", personList.get().absolutePath());

    // Test indexed element path
    Optional<NavigableTreeNode> firstPerson = personList.get().getChild("0");
    assertTrue(firstPerson.isPresent());
    assertEquals("/person/0", firstPerson.get().absolutePath());

    // Test attribute path
    Optional<NavigableTreeNode> nameAttr = firstPerson.get().getChild("@name");
    assertTrue(nameAttr.isPresent());
    assertEquals("/person/0/@name", nameAttr.get().absolutePath());
  }

  @Test
  void testGetRoot() {
    // Root should return itself
    assertEquals(rootNode, rootNode.getRoot());

    // Child should return root
    Optional<NavigableTreeNode> child = rootNode.getChild("person");
    assertTrue(child.isPresent());
    assertEquals(rootNode, child.get().getRoot());

    // Nested child should return root
    Optional<NavigableTreeNode> nestedChild = child.get().getChild("0");
    assertTrue(nestedChild.isPresent());
    assertEquals(rootNode, nestedChild.get().getRoot());
  }

  @Test
  void testElementWithTextContentAndAttributes() {
    Element metadataElement = rootElement.element("metadata");
    NavigableXmlNode metadataNode = new NavigableXmlNode(metadataElement, rootNode, 0);

    // Should be able to get text content
    Optional<NavigableTreeNode> textNode = metadataNode.getChild("text()");
    assertTrue(textNode.isPresent());
    assertEquals("Some metadata text", textNode.get().getValue().getValue());

    // Children should include text() if present
    List<NavigableTreeNode> children = metadataNode.children().collect(Collectors.toList());
    boolean hasTextChild =
        children.stream().anyMatch(child -> "text()".equals(child.getName().getValue()));
    assertTrue(hasTextChild);
  }
}
