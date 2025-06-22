package io.broessl.treesql.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.core.types.TreeString;

public class ScannableTreeNode implements Iterable<ScannableTreeNode> {

	static final Iterator<ScannableTreeNode> EMPTY_SCAN = new Iterator<ScannableTreeNode>() {
		@Override
		public ScannableTreeNode next() {
			throw new NoSuchElementException();
		}

		@Override
		public boolean hasNext() {
			return false;
		}
	};

	private class DepthFirstIterator implements Iterator<ScannableTreeNode> {

		List<Iterator<ScannableTreeNode>> deeperScans = new ArrayList<>();

		List<TreeScanExpression> interpolatedScanExpressions;

		public DepthFirstIterator() {
			interpolatedScanExpressions = ScannableTreeNode.this.scanExpression
					.interpolateExpression(ScannableTreeNode.this.node);
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
				result.add((List.of(ScannableTreeNode.this).iterator()));
				deeperScans = result;
				return;
			}
			switch (expression.currentStep()) {
				case TreeScanStep.LiteralForwardStep j -> {
					ScannableTreeNode nextNodeByStep = ScannableTreeNode.this.get(j.getRaw(),
							expression.subExpression(), ScannableTreeNode.this.bindings.chain(new TreeString(j.getRaw())));
					result.add(nextNodeByStep != null ? nextNodeByStep.iterator() : EMPTY_SCAN);
				}
				case TreeScanStep.SingleForwardStep j2 -> {
					result.addAll(ScannableTreeNode.this.childrenScan(expression.subExpression(),
							ScannableTreeNode.this.bindings, j2.getRangeLiteral()));
				}
				case TreeScanStep.SingleBackStep j4 -> {
					result.add(ScannableTreeNode.this.backScan(expression.subExpression(),
							ScannableTreeNode.this.bindings, j4.getRangeLiteral()));
				}
				case TreeScanStep.SingleSideStep j6 -> {
					result.add(ScannableTreeNode.this.offsetScan(expression.subExpression(),
							ScannableTreeNode.this.bindings, j6.getIndexManipulation()));
				}
				default -> {
					throw new IllegalStateException("unexpected next step");
				}
			}
			deeperScans = result;
		}

		@Override
		public boolean hasNext() {
			while (!interpolatedScanExpressions.isEmpty() || !deeperScans.isEmpty()) {
				if (deeperScans.getFirst().hasNext()) {
					return true;
				}
				// try with next deep scan
				deeperScans.removeFirst();
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
			ScannableTreeNode next = deeperScans.getFirst().next();
			return new ScannableTreeNode(next.node, next.bindings.asImmutable());
		}
	}

	private ScanContext bindings;

	private TreeScanExpression scanExpression;

	final NavigableTreeNode node;

	public ScannableTreeNode(NavigableTreeNode node, ScanContext bindings) {
		this.bindings = bindings;
		this.node = node;
	}

	public ScannableTreeNode(NavigableTreeNode node, ScanContext bindings, TreeScanExpression scanExpression) {
		this.bindings = bindings;
		this.node = node;
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

	public ScannableTreeNode get(String nameOrIndex, TreeScanExpression scanExpression, ScanContext bindings) {
		NavigableTreeNode forth = node.getNextNode(nameOrIndex).orElse(null);
		if (forth == null) {
			return null;
		}
		return new ScannableTreeNode(forth, bindings, scanExpression);
	}

	public List<Iterator<ScannableTreeNode>> childrenScan(TreeScanExpression subExpression, ScanContext subBindings,
			Optional<String> rangeLiteral) {
		final String alreadyBound;
		if (rangeLiteral.isPresent()) {
			alreadyBound = (String) subBindings.getBinding(rangeLiteral.get());
		} else {
			alreadyBound = null;
		}
		return node.getAllNextNodes().filter(refNode -> {
			if (alreadyBound != null) {
				return false;
			} else {
				return true;
			}
		}).map(refNode -> {
			TreeNodeIdentifier nameOrIndex = refNode.getNameOrIndex();
			if (rangeLiteral.isPresent()) {
				return new ScannableTreeNode(refNode,
						subBindings.chainWithPathBinding(nameOrIndex, rangeLiteral.get(), refNode),
						subExpression)
						.iterator();
			}
			return new ScannableTreeNode(refNode, subBindings.chain(nameOrIndex), subExpression).iterator();
		}).toList();
	}

	public Iterator<ScannableTreeNode> backScan(TreeScanExpression subExpression, ScanContext subBindings) {
		NavigableTreeNode parent = node.getPreviousNode().orElse(null);
		if (parent == null) {
			return EMPTY_SCAN;
		}
		return new ScannableTreeNode(node.getPreviousNode().orElseThrow(), subBindings, subExpression).iterator();
	}

	public Iterator<ScannableTreeNode> backScan(TreeScanExpression subExpression, ScanContext currentBindings,
			Optional<String> rangeLiteral) {
		try {
			NavigableTreeNode parent = node.getPreviousNode().orElse(null);
			if (parent == null) {
				return EMPTY_SCAN;
			}
			TreeNodeIdentifier nodeName = parent.getNameOrIndex();
			if (rangeLiteral.isPresent()) {
				TreeNodeIdentifier alreadyBound = (TreeNodeIdentifier) currentBindings.getBinding(rangeLiteral.get());
				if (alreadyBound != null && !alreadyBound.equals(nodeName)) {
					return EMPTY_SCAN;
				}
				if (alreadyBound == null) {
					return new ScannableTreeNode(node.getPreviousNode().orElseThrow(),
							currentBindings.chainWithPathBinding(new TreeString("~.."), rangeLiteral.get(),
									node.getPreviousNode().get()),
							subExpression).iterator();
				}
			}
			return new ScannableTreeNode(node.getPreviousNode().orElseThrow(),
					currentBindings.chain(new TreeString("~..")),
					subExpression).iterator();
		} catch (NoSuchElementException e) {
			return EMPTY_SCAN;
		}
	}

	public Iterator<ScannableTreeNode> offsetScan(TreeScanExpression subExpression, ScanContext subBindings,
			Integer indexManipulation) {
		NavigableTreeNode sibling = node.getSibling(indexManipulation).orElse(null);
		if (sibling == null) {
			return EMPTY_SCAN;
		}
		return new ScannableTreeNode(sibling, subBindings.chain(new TreeString("[" + indexManipulation + "]~")),
				subExpression)
				.iterator();
	}

	@Override
	public Iterator<ScannableTreeNode> iterator() {
		return new DepthFirstIterator();
	}

	@Override
	public String toString() {
		return node.toString();
	}

	public NavigableTreeNode getNavigableTreeNode() {
		return node;
	}

	public ScanContext getContext() {
		return bindings;
	}

	public void setContext(ScanContext bindings) {
		this.bindings = bindings.asMutable();
	}

	public String absolutePath() {
		return node.absolutePath();
	}

	public TreeNodeIdentifier getNameOrIndex() {
		return node.getNameOrIndex();
	}

}
