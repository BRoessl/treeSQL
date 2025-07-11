package io.broessl.treesql.core;

import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.spi.NavigableTree;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/** Handles various tree scanning operations for ScannableTreeNode. */
class TreeScanOperations {

  static final Iterator<ScannableTreeNode> EMPTY_SCAN =
      new Iterator<ScannableTreeNode>() {
        @Override
        public ScannableTreeNode next() {
          throw new NoSuchElementException();
        }

        @Override
        public boolean hasNext() {
          return false;
        }
      };

  private final ScannableTreeNode scannableNode;

  public TreeScanOperations(ScannableTreeNode scannableNode) {
    this.scannableNode = scannableNode;
  }

  public Iterator<ScannableTreeNode> literalForwardScan(
      TreeScanExpression expression, TreeScanStep.LiteralForwardStep step) {
    ScannableTreeNode nextNodeByStep;

    if (step.getRangeLiteral().isPresent()) {
      nextNodeByStep =
          scannableNode.get(
              step.getRaw(),
              expression.subExpression(),
              scannableNode
                  .getContext()
                  .update(
                      step.getRangeLiteral().get(),
                      scannableNode.getNavigableTreeNode().getChild(step.getRaw()).get()));
    } else {
      nextNodeByStep =
          scannableNode.get(step.getRaw(), expression.subExpression(), scannableNode.getContext());
    }

    return nextNodeByStep != null ? nextNodeByStep.iterator() : EMPTY_SCAN;
  }

  public List<Iterator<ScannableTreeNode>> childrenScan(
      TreeScanExpression expression, TreeScanStep.RangedForwardStep step) {
    return childrenScan(
        expression.subExpression(), scannableNode.getContext(), step.getRangeLiteral());
  }

  public Iterator<ScannableTreeNode> backScan(
      TreeScanExpression expression, TreeScanStep.SingleBackStep step) {
    return backScan(expression.subExpression(), scannableNode.getContext(), step.getRangeLiteral());
  }

  public Iterator<ScannableTreeNode> offsetScan(
      TreeScanExpression expression, TreeScanStep.SingleSideStep step) {
    return offsetScan(
        expression.subExpression(),
        scannableNode.getContext(),
        step.getIndexManipulation(),
        step.getRangeLiteral());
  }

  public Iterator<ScannableTreeNode> executeDirective(
      TreeScanExpression expression, TreeScanStep.DirectiveStep step) {
    return executeDirective(
        step.getRangeLiteral().get(),
        expression.subExpression(),
        scannableNode.getContext(),
        step.getRaw(),
        step.getArguments());
  }

  public List<Iterator<ScannableTreeNode>> childrenScan(
      TreeScanExpression subExpression, ScanContext subBindings, Optional<String> rangeLiteral) {
    final TreeNodeIdentifier alreadyBound;
    if (rangeLiteral.isPresent()) {
      alreadyBound = subBindings.getNodeName(rangeLiteral.get());
    } else {
      alreadyBound = null;
    }

    return scannableNode
        .getNavigableTreeNode()
        .children()
        .filter(refNode -> alreadyBound == null)
        .map(
            refNode -> {
              if (rangeLiteral.isPresent()) {
                return new ScannableTreeNode(
                        refNode, subBindings.update(rangeLiteral.get(), refNode), subExpression)
                    .iterator();
              }
              return new ScannableTreeNode(refNode, subBindings, subExpression).iterator();
            })
        .toList();
  }

  public Iterator<ScannableTreeNode> backScan(
      TreeScanExpression subExpression,
      ScanContext currentBindings,
      Optional<String> rangeLiteral) {
    try {
      NavigableTreeNode parent = scannableNode.getNavigableTreeNode().getParent().orElse(null);
      if (parent == null) {
        return EMPTY_SCAN;
      }

      TreeNodeIdentifier nodeName = parent.getName();
      if (rangeLiteral.isPresent()) {
        TreeNodeIdentifier alreadyBound = currentBindings.getNodeName(rangeLiteral.get());
        if (alreadyBound != null && !alreadyBound.equals(nodeName)) {
          return EMPTY_SCAN;
        }
        if (alreadyBound == null) {
          return new ScannableTreeNode(
                  scannableNode.getNavigableTreeNode().getParent().orElseThrow(),
                  currentBindings.update(
                      rangeLiteral.get(), scannableNode.getNavigableTreeNode().getParent().get()),
                  subExpression)
              .iterator();
        }
      }
      return new ScannableTreeNode(
              scannableNode.getNavigableTreeNode().getParent().orElseThrow(),
              currentBindings,
              subExpression)
          .iterator();
    } catch (NoSuchElementException e) {
      return EMPTY_SCAN;
    }
  }

  public Iterator<ScannableTreeNode> executeDirective(
      String rootName,
      TreeScanExpression subExpression,
      ScanContext currentBindings,
      String directive,
      List<String> arguments) {
    Optional<NavigableTreeNode> virtualChild =
        NavigableTree.providerFor(directive)
            .attachTreeNode(
                rootName,
                scannableNode.getNavigableTreeNode().getValue(),
                scannableNode.getNavigableTreeNode(),
                arguments);
    if (virtualChild.isEmpty()) {
      return EMPTY_SCAN;
    }
    return new ScannableTreeNode(virtualChild.get(), currentBindings, subExpression).iterator();
  }

  public Iterator<ScannableTreeNode> offsetScan(
      TreeScanExpression subExpression,
      ScanContext subBindings,
      Integer indexManipulation,
      Optional<String> rangeLiteral) {
    NavigableTreeNode sibling =
        scannableNode.getNavigableTreeNode().getSibling(indexManipulation).orElse(null);
    if (sibling == null) {
      return EMPTY_SCAN;
    }

    if (rangeLiteral.isEmpty()) {
      return new ScannableTreeNode(sibling, subBindings, subExpression).iterator();
    } else {
      return new ScannableTreeNode(
              sibling, subBindings.update(rangeLiteral.get(), sibling), subExpression)
          .iterator();
    }
  }
}
