package io.broessl.treesql.core;

import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.core.types.TreeString;
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

  private final ScannableTreeNode rootNode;

  public TreeScanOperations(ScannableTreeNode rootNode) {
    this.rootNode = rootNode;
  }

  public Iterator<ScannableTreeNode> literalForwardScan(
      TreeScanExpression expression, TreeScanStep.LiteralForwardStep step) {
    ScannableTreeNode nextNodeByStep;

    if (step.getRangeLiteral().isPresent()) {
      nextNodeByStep =
          rootNode.get(
              step.getRaw(),
              expression.subExpression(),
              rootNode
                  .getContext()
                  .chainWithPathBinding(
                      new TreeString(step.getRaw()),
                      step.getRangeLiteral().get(),
                      rootNode.getNavigableTreeNode().getChildNode(step.getRaw()).get()));
    } else {
      nextNodeByStep =
          rootNode.get(
              step.getRaw(),
              expression.subExpression(),
              rootNode.getContext().chain(new TreeString(step.getRaw())));
    }

    return nextNodeByStep != null ? nextNodeByStep.iterator() : EMPTY_SCAN;
  }

  public List<Iterator<ScannableTreeNode>> childrenScan(
      TreeScanExpression expression, TreeScanStep.RangedForwardStep step) {
    return childrenScan(expression.subExpression(), rootNode.getContext(), step.getRangeLiteral());
  }

  public Iterator<ScannableTreeNode> backScan(
      TreeScanExpression expression, TreeScanStep.SingleBackStep step) {
    return backScan(expression.subExpression(), rootNode.getContext(), step.getRangeLiteral());
  }

  public Iterator<ScannableTreeNode> offsetScan(
      TreeScanExpression expression, TreeScanStep.SingleSideStep step) {
    return offsetScan(
        expression.subExpression(),
        rootNode.getContext(),
        step.getIndexManipulation(),
        step.getRangeLiteral());
  }

  public Iterator<ScannableTreeNode> executeDirective(
      TreeScanExpression expression, TreeScanStep.DirectiveStep step) {
    return executeDirective(expression.subExpression(), rootNode.getContext(), step.getRaw());
  }

  public List<Iterator<ScannableTreeNode>> childrenScan(
      TreeScanExpression subExpression, ScanContext subBindings, Optional<String> rangeLiteral) {
    final String alreadyBound;
    if (rangeLiteral.isPresent()) {
      alreadyBound = (String) subBindings.getBinding(rangeLiteral.get());
    } else {
      alreadyBound = null;
    }

    return rootNode
        .getNavigableTreeNode()
        .children()
        .filter(refNode -> alreadyBound == null)
        .map(
            refNode -> {
              TreeNodeIdentifier nameOrIndex = refNode.getSelfName();
              if (rangeLiteral.isPresent()) {
                return new ScannableTreeNode(
                        refNode,
                        subBindings.chainWithPathBinding(nameOrIndex, rangeLiteral.get(), refNode),
                        subExpression)
                    .iterator();
              }
              return new ScannableTreeNode(refNode, subBindings.chain(nameOrIndex), subExpression)
                  .iterator();
            })
        .toList();
  }

  public Iterator<ScannableTreeNode> backScan(
      TreeScanExpression subExpression,
      ScanContext currentBindings,
      Optional<String> rangeLiteral) {
    try {
      NavigableTreeNode parent = rootNode.getNavigableTreeNode().getParentNode().orElse(null);
      if (parent == null) {
        return EMPTY_SCAN;
      }

      TreeNodeIdentifier nodeName = parent.getSelfName();
      if (rangeLiteral.isPresent()) {
        TreeNodeIdentifier alreadyBound =
            (TreeNodeIdentifier) currentBindings.getBinding(rangeLiteral.get());
        if (alreadyBound != null && !alreadyBound.equals(nodeName)) {
          return EMPTY_SCAN;
        }
        if (alreadyBound == null) {
          return new ScannableTreeNode(
                  rootNode.getNavigableTreeNode().getParentNode().orElseThrow(),
                  currentBindings.chainWithPathBinding(
                      new TreeString("~.."),
                      rangeLiteral.get(),
                      rootNode.getNavigableTreeNode().getParentNode().get()),
                  subExpression)
              .iterator();
        }
      }
      return new ScannableTreeNode(
              rootNode.getNavigableTreeNode().getParentNode().orElseThrow(),
              currentBindings.chain(new TreeString("~..")),
              subExpression)
          .iterator();
    } catch (NoSuchElementException e) {
      return EMPTY_SCAN;
    }
  }

  public Iterator<ScannableTreeNode> executeDirective(
      TreeScanExpression subExpression, ScanContext currentBindings, String directive) {
    var registered = DirectiveRegistry.INSTANCE.getDirective(directive);
    if (registered == null) {
      return EMPTY_SCAN;
    }
    var virtualChild = registered.apply(rootNode.getNavigableTreeNode());
    if (virtualChild == null) {
      return EMPTY_SCAN;
    }
    String virtualName = virtualChild.getSelfName().toString();
    return new ScannableTreeNode(
            virtualChild, currentBindings.chain(new TreeString(virtualName)), subExpression)
        .iterator();
  }

  public Iterator<ScannableTreeNode> offsetScan(
      TreeScanExpression subExpression,
      ScanContext subBindings,
      Integer indexManipulation,
      Optional<String> rangeLiteral) {
    NavigableTreeNode sibling =
        rootNode.getNavigableTreeNode().getSibling(indexManipulation).orElse(null);
    if (sibling == null) {
      return EMPTY_SCAN;
    }

    if (rangeLiteral.isEmpty()) {
      return new ScannableTreeNode(
              sibling,
              subBindings.chain(new TreeString("[" + indexManipulation + "]~")),
              subExpression)
          .iterator();
    } else {
      return new ScannableTreeNode(
              sibling,
              subBindings.chainWithPathBinding(
                  new TreeString("[" + indexManipulation + "]~"), rangeLiteral.get(), sibling),
              subExpression)
          .iterator();
    }
  }
}
