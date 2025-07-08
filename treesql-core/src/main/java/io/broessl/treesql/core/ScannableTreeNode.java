package io.broessl.treesql.core;

import io.broessl.treesql.core.types.TreeNodeIdentifier;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A scannable wrapper around a NavigableTreeNode that provides tree scanning capabilities with
 * support for expressions and context binding.
 */
public class ScannableTreeNode implements Iterable<ScannableTreeNode> {

  private ScanContext bindings;
  private TreeScanExpression scanExpression;
  private final NavigableTreeNode navigableNode;

  public ScannableTreeNode(NavigableTreeNode node, ScanContext bindings) {
    this.bindings = bindings;
    this.navigableNode = node;
  }

  public ScannableTreeNode(
      NavigableTreeNode node, ScanContext bindings, TreeScanExpression scanExpression) {
    this.bindings = bindings;
    this.navigableNode = node;
    this.scanExpression = scanExpression;
  }

  public static ScannableTreeNode forRoot(NavigableTreeNode rootNode) {
    return new ScannableTreeNode(rootNode, new TransientScanContext());
  }

  public Stream<ScannableTreeNode> scan(String expression) {
    this.scanExpression = TreeScanExpression.parse(expression);
    this.bindings = this.bindings.asMutable();
    return StreamSupport.stream(this.spliterator(), false);
  }

  public Stream<ScannableTreeNode> scan() {
    if (this.scanExpression == null) {
      throw new IllegalStateException("no scan expression set");
    }
    return StreamSupport.stream(this.spliterator(), false);
  }

  public ScannableTreeNode get(
      String nameOrIndex, TreeScanExpression scanExpression, ScanContext bindings) {
    NavigableTreeNode forth = navigableNode.getChild(nameOrIndex).orElse(null);
    if (forth == null) {
      return null;
    }
    return new ScannableTreeNode(forth, bindings, scanExpression);
  }

  @Override
  public Iterator<ScannableTreeNode> iterator() {
    if (this.scanExpression == null) {
      throw new IllegalStateException("no scan expression set");
    }
    return new TreeNodeIterator(this, new TreeScanOperations(this));
  }

  @Override
  public String toString() {
    return navigableNode.toString();
  }

  public NavigableTreeNode getNavigableTreeNode() {
    return navigableNode;
  }

  public ScanContext getContext() {
    return bindings;
  }

  public TreeScanExpression getScanExpression() {
    return scanExpression;
  }

  public void setContext(ScanContext bindings) {
    this.bindings = bindings.asMutable();
  }

  public String absolutePath() {
    return navigableNode.absolutePath();
  }

  public TreeNodeIdentifier getName() {
    return navigableNode.getName();
  }
}
