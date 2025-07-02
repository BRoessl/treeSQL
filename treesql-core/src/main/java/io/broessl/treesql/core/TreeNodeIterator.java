package io.broessl.treesql.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/** Iterator implementation for depth-first traversal of ScannableTreeNode instances. */
class TreeNodeIterator implements Iterator<ScannableTreeNode> {

  private final ScannableTreeNode rootNode;
  private final TreeScanOperations scanOperations;

  private List<Iterator<ScannableTreeNode>> deeperScans = new ArrayList<>();
  private List<TreeScanExpression> interpolatedScanExpressions;

  public TreeNodeIterator(ScannableTreeNode rootNode, TreeScanOperations scanOperations) {
    this.rootNode = rootNode;
    this.scanOperations = scanOperations;
    this.interpolatedScanExpressions =
        rootNode.getScanExpression().interpolateExpression(rootNode.getNavigableTreeNode());
    deepScansForNextExpression();
  }

  private void deepScansForNextExpression() {
    if (interpolatedScanExpressions.isEmpty()) {
      deeperScans = List.of();
      return;
    }
    List<Iterator<ScannableTreeNode>> result = new LinkedList<>();
    TreeScanExpression expression = interpolatedScanExpressions.removeFirst();

    if (expression.matches()) {
      result.add(List.of(rootNode).iterator());
      deeperScans = result;
      return;
    }

    processExpression(expression, result);
    deeperScans = result;
  }

  private void processExpression(
      TreeScanExpression expression, List<Iterator<ScannableTreeNode>> result) {
    switch (expression.currentStep()) {
      case TreeScanStep.LiteralForwardStep j ->
          result.add(scanOperations.literalForwardScan(expression, j));
      case TreeScanStep.SingleForwardStep j2 ->
          result.addAll(scanOperations.childrenScan(expression, j2));
      case TreeScanStep.SingleBackStep j4 -> result.add(scanOperations.backScan(expression, j4));
      case TreeScanStep.SingleSideStep j6 -> result.add(scanOperations.offsetScan(expression, j6));
      case TreeScanStep.DirectiveStep j7 ->
          result.add(scanOperations.executeDirective(expression, j7));
      default -> throw new IllegalStateException("unexpected next step");
    }
  }

  @Override
  public boolean hasNext() {
    while (!interpolatedScanExpressions.isEmpty() || !deeperScans.isEmpty()) {
      if (!deeperScans.isEmpty() && deeperScans.getFirst().hasNext()) {
        return true;
      }
      // try with next deep scan
      if (!deeperScans.isEmpty()) {
        deeperScans.removeFirst();
      }
      if (deeperScans.isEmpty()) {
        // try again with next expression
        deepScansForNextExpression();
      }
    }
    // no deeper scan available
    return false;
  }

  @Override
  public ScannableTreeNode next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    ScannableTreeNode next = deeperScans.getFirst().next();
    return new ScannableTreeNode(next.getNavigableTreeNode(), next.getContext().asImmutable());
  }
}
