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
      case TreeScanStep.LiteralForwardStep literalStep ->
          result.add(scanOperations.literalForwardScan(expression, literalStep));
      case TreeScanStep.RangedForwardStep rangeStep ->
          result.addAll(scanOperations.childrenScan(expression, rangeStep));
      case TreeScanStep.SingleBackStep backStep ->
          result.add(scanOperations.backScan(expression, backStep));
      case TreeScanStep.SingleSideStep sideStep ->
          result.add(scanOperations.offsetScan(expression, sideStep));
      case TreeScanStep.DirectiveStep directive ->
          result.add(scanOperations.executeDirective(expression, directive));
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
    return new ScannableTreeNode(next.getNavigableTreeNode(), new ScanContext(next.getContext()));
  }
}
