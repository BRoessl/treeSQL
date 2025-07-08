package io.broessl.treesql.xml;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.NavigableTreeNode;
import java.util.Optional;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.jupiter.api.Test;

/** Integration tests demonstrating real-world XML navigation scenarios */
public class NavigableXmlNodeIntegrationTest {

  @Test
  void testNavigateComplexXmlStructure() throws Exception {
    String xmlString =
        """
            <library>
                <book id="book1" category="fiction">
                    <title>The Great Gatsby</title>
                    <author>F. Scott Fitzgerald</author>
                    <year>1925</year>
                    <reviews>
                        <review rating="5">Excellent classic</review>
                        <review rating="4">Good read</review>
                    </reviews>
                </book>
                <book id="book2" category="science">
                    <title>A Brief History of Time</title>
                    <author>Stephen Hawking</author>
                    <year>1988</year>
                </book>
            </library>
            """;

    Document document = DocumentHelper.parseText(xmlString);
    NavigableXmlNode libraryNode = new NavigableXmlNode(document.getRootElement(), null, "library");

    // Navigate to first book
    Optional<NavigableTreeNode> booksNode = libraryNode.getChildNode("book");
    assertTrue(booksNode.isPresent());
    assertTrue(booksNode.get().isListNode());
    assertEquals(2, booksNode.get().getSize().orElse(-1));

    // Get first book
    Optional<NavigableTreeNode> firstBook = booksNode.get().getChildNode("0");
    assertTrue(firstBook.isPresent());

    // Check book attributes
    Optional<NavigableTreeNode> bookId = firstBook.get().getChildNode("@id");
    assertTrue(bookId.isPresent());
    assertEquals("book1", bookId.get().getValue().getValue());

    Optional<NavigableTreeNode> category = firstBook.get().getChildNode("@category");
    assertTrue(category.isPresent());
    assertEquals("fiction", category.get().getValue().getValue());

    // Navigate to title
    Optional<NavigableTreeNode> titleNode = firstBook.get().getChildNode("title");
    assertTrue(titleNode.isPresent());

    Optional<NavigableTreeNode> titleText =
        titleNode.get().getChildNode("0").flatMap(t -> t.getChildNode("text()"));
    assertTrue(titleText.isPresent());
    assertEquals("The Great Gatsby", titleText.get().getValue().getValue());

    // Navigate to reviews
    Optional<NavigableTreeNode> reviewsNode = firstBook.get().getChildNode("reviews");
    assertTrue(reviewsNode.isPresent());

    Optional<NavigableTreeNode> reviewsList =
        reviewsNode.get().getChildNode("0").flatMap(r -> r.getChildNode("review"));
    assertTrue(reviewsList.isPresent());
    assertEquals(2, reviewsList.get().getSize().orElse(-1));

    // Get first review
    Optional<NavigableTreeNode> firstReview = reviewsList.get().getChildNode("0");
    assertTrue(firstReview.isPresent());

    Optional<NavigableTreeNode> rating = firstReview.get().getChildNode("@rating");
    assertTrue(rating.isPresent());
    assertEquals("5", rating.get().getValue().getValue());

    Optional<NavigableTreeNode> reviewText = firstReview.get().getChildNode("text()");
    assertTrue(reviewText.isPresent());
    assertEquals("Excellent classic", reviewText.get().getValue().getValue());

    // Test sibling navigation within reviews
    Optional<NavigableTreeNode> secondReview = firstReview.get().getSibling(1);
    assertTrue(secondReview.isPresent());

    Optional<NavigableTreeNode> secondRating = secondReview.get().getChildNode("@rating");
    assertTrue(secondRating.isPresent());
    assertEquals("4", secondRating.get().getValue().getValue());
  }

  @Test
  void testAbsolutePathGeneration() throws Exception {
    String xmlString =
        """
            <catalog>
                <product id="p1">
                    <name>Widget</name>
                    <specs>
                        <dimension unit="cm">10x5x2</dimension>
                    </specs>
                </product>
            </catalog>
            """;

    Document document = DocumentHelper.parseText(xmlString);
    NavigableXmlNode catalogNode = new NavigableXmlNode(document.getRootElement(), null, "catalog");

    // Test various absolute paths
    assertEquals("", catalogNode.absolutePath());

    NavigableTreeNode productList = catalogNode.getChildNode("product").orElseThrow();
    assertEquals("/product", productList.absolutePath());

    NavigableTreeNode firstProduct = productList.getChildNode("0").orElseThrow();
    assertEquals("/product/0", firstProduct.absolutePath());

    NavigableTreeNode productId = firstProduct.getChildNode("@id").orElseThrow();
    assertEquals("/product/0/@id", productId.absolutePath());

    NavigableTreeNode nameList = firstProduct.getChildNode("name").orElseThrow();
    assertEquals("/product/0/name", nameList.absolutePath());

    NavigableTreeNode nameElement = nameList.getChildNode("0").orElseThrow();
    assertEquals("/product/0/name/0", nameElement.absolutePath());

    NavigableTreeNode nameText = nameElement.getChildNode("text()").orElseThrow();
    assertEquals("/product/0/name/0/text()", nameText.absolutePath());
  }

  @Test
  void testRootNodeBehavior() throws Exception {
    String xmlString = "<root><child>value</child></root>";
    Document document = DocumentHelper.parseText(xmlString);
    NavigableXmlNode rootNode = new NavigableXmlNode(document.getRootElement(), null, "root");

    // Root node should be its own root
    assertEquals(rootNode, rootNode.getRoot());

    // Child should have root as its root
    NavigableTreeNode child = rootNode.getChildNode("child").orElseThrow();
    assertEquals(rootNode, child.getRoot());

    // Nested child should also have root as its root
    NavigableTreeNode nestedChild = child.getChildNode("0").orElseThrow();
    assertEquals(rootNode, nestedChild.getRoot());
  }
}
