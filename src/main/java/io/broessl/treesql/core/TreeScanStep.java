package io.broessl.treesql.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract sealed class TreeScanStep {

	static final Pattern ANON_FORWARD;

	static final Pattern NAMED_FORWARD;

	static final Pattern ANON_BACK;

	static final Pattern NAMED_BACK;

	static final Pattern ANON_DEPTH_SCAN;

	static final Pattern ANON_LEVEL_SCAN;

	static final Pattern NAMED_DEPTH_SCAN;

	static final Pattern NAMED_LEVEL_SCAN;

	static final Pattern ANON_SIBLING;

	static final Pattern NAMED_SIBLING;


	static {
		ANON_FORWARD = Pattern.compile("^~$");
		NAMED_FORWARD = Pattern.compile("^~([a-z]\\w+)$");

		ANON_BACK = Pattern.compile("^\\.\\.~$");
		NAMED_BACK = Pattern.compile("^\\.\\.~([a-z]\\w+)$");

		ANON_SIBLING = Pattern.compile("^\\[(0|[+-][\\d]+)\\]~$");
		NAMED_SIBLING = Pattern.compile("^\\[(0|[+-][\\d]+)\\]~([a-z]\\w*)$");

		ANON_DEPTH_SCAN = Pattern.compile("^\\{(-?\\d+),(-?\\d+)\\}~$");
		NAMED_DEPTH_SCAN = Pattern.compile("^\\{(-?\\d+),(-?\\d+)\\}~([a-z]\\w+)$");

		ANON_LEVEL_SCAN = Pattern.compile("^\\[(0|[+-][\\d]+),(0|[+-][\\d]+)\\]~$");
		NAMED_LEVEL_SCAN = Pattern.compile("^\\[(0|[+-][\\d]+),(0|[+-][\\d]+)\\]~([a-z]\\w+)$");
	}

	private String raw;

	private Optional<String> rangeLiteral;

	public final Optional<String> getRangeLiteral() {
		return rangeLiteral;
	}

	protected void setRangeLiteral(String rangeLiteral) {
		this.rangeLiteral = rangeLiteral == null || rangeLiteral.isBlank() ? Optional.empty()
				: Optional.of(rangeLiteral);
	}

	public final String getRaw() {
		return raw;
	}

	public boolean isLiteral() {
		return false;
	}

	public boolean ranged() {
		return false;
	}

	public ScanBoundaries getBoundaries() {
		throw new UnsupportedOperationException("boundaries supported for ranged=true only");
	}

	TreeScanStep(String raw) {
		this.raw = raw;
		rangeLiteral = Optional.empty();
	}

	TreeScanStep(String raw, String rangeLiteral) {
		this.raw = raw;
		this.rangeLiteral = rangeLiteral == null || rangeLiteral.isBlank() ? Optional.empty()
				: Optional.of(rangeLiteral);
	}

	public static TreeScanStep literal(String literal) {
		return new LiteralForwardStep(literal);
	}

	public static TreeScanStep pattern(String raw, Function<String, TreeScanStep> fallback) {
		Matcher matcher = ANON_FORWARD.matcher(raw);
		if (matcher.matches()) {
			return new SingleForwardStep(raw, null);
		}
		matcher = NAMED_FORWARD.matcher(raw);
		if (matcher.matches()) {
			return new SingleForwardStep(raw, matcher.group(1));
		}
		matcher = ANON_BACK.matcher(raw);
		if (matcher.matches()) {
			return new SingleBackStep(raw, null);
		}
		matcher = NAMED_BACK.matcher(raw);
		if (matcher.matches()) {
			return new SingleBackStep(raw, matcher.group(1));
		}
		matcher = ANON_DEPTH_SCAN.matcher(raw);
		if (matcher.matches()) {
			ScanBoundaries boundaries = new ScanBoundaries(matcher.group(1), matcher.group(2), matcher.group(0));
			return new DepthScan(raw, boundaries);
		}
		matcher = NAMED_DEPTH_SCAN.matcher(raw);
		if (matcher.matches()) {
			ScanBoundaries boundaries = new ScanBoundaries(matcher.group(1), matcher.group(2), matcher.group(0));
			return new DepthScan(raw, boundaries, matcher.group(3));
		}
		matcher = ANON_SIBLING.matcher(raw);
		if (matcher.matches()) {
			ScanBoundaries boundaries = new ScanBoundaries(matcher.group(1), matcher.group(1), matcher.group(0));
			return new SingleSideStep(raw, null, boundaries.startInclusive);
		}
		matcher = NAMED_SIBLING.matcher(raw);
		if (matcher.matches()) {
			ScanBoundaries boundaries = new ScanBoundaries(matcher.group(1), matcher.group(1), matcher.group(0));
			String rangeLiteral = matcher.group(2);
			return new SingleSideStep(raw, rangeLiteral, boundaries.startInclusive);
		}
		matcher = ANON_LEVEL_SCAN.matcher(raw);
		if (matcher.matches()) {
			ScanBoundaries boundaries = new ScanBoundaries(matcher.group(1), matcher.group(2), matcher.group(0));
			return new LevelScan(raw, null, boundaries);
		}
		matcher = NAMED_LEVEL_SCAN.matcher(raw);
		if (matcher.matches()) {
			ScanBoundaries boundaries = new ScanBoundaries(matcher.group(1), matcher.group(2), matcher.group(0));
			String rangeLiteral = matcher.group(3);
			return new LevelScan(raw, rangeLiteral, boundaries);
		}
		if (fallback == null) {
			throw new IllegalArgumentException("Unknown pattern for '" + raw + "'.");
		} else {
			return fallback.apply(raw);
		}
	}

	@Override
	public String toString() {
		return raw;
	}

	static final class LiteralForwardStep extends TreeScanStep {
		LiteralForwardStep(String literal) {
			super(literal);
		}

		@Override
		public boolean isLiteral() {
			return true;
		}

		public void updateWithOffset(int i) {
			try {
				super.raw = "" + (Integer.parseInt(getRaw()) + i);
			} catch (Exception e) {
				throw new IllegalArgumentException("step might not be a literal indexed step", e);
			}
		}
	}

	static final class SingleForwardStep extends TreeScanStep {

		SingleForwardStep(String raw, String rangeLiteral) {
			super(raw, rangeLiteral);
		}

		SingleForwardStep() {
			this("~", null);
		}

	}

	static final class DepthScan extends TreeScanStep {

		private ScanBoundaries boundaries;
		private String nameForLastStep;

		DepthScan(String raw, ScanBoundaries boundaries) {
			super(raw);
			this.boundaries = boundaries;
		}

		public DepthScan(String raw, ScanBoundaries boundaries, String nameForLastStep) {
			this(raw, boundaries);
			this.nameForLastStep = nameForLastStep;
		}

		@Override
		public ScanBoundaries getBoundaries() {
			return boundaries;
		}

		@Override
		public boolean ranged() {
			return true;
		}

		public List<List<TreeScanStep>> interpolate() {
			List<List<TreeScanStep>> result = new LinkedList<>();
			if (boundaries.startInclusive.equals(boundaries.endInclusive)) {
				int fixed = boundaries.startInclusive;
				result.add(toSingleSteps(fixed));
			}
			if (boundaries.startInclusive > boundaries.endInclusive) {
				int upper = boundaries.startInclusive;
				int lower = boundaries.endInclusive;
				for (int i = upper; i >= lower; i--) {
					result.add(toSingleSteps(i));
				}
			}
			if (boundaries.startInclusive < boundaries.endInclusive) {
				int upper = boundaries.endInclusive;
				int lower = boundaries.startInclusive;
				for (int i = lower; i <= upper; i++) {
					result.add(toSingleSteps(i));
				}
			}
			return result;
		}

		private List<TreeScanStep> toSingleSteps(int size) {
			ArrayList<TreeScanStep> pre;
			if (size >= 0) {
				pre = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					pre.add(new TreeScanStep.SingleForwardStep());
				}
			} else {
				pre = new ArrayList<>(size * -1);
				for (int i = 0; i > size; i--) {
					pre.add(new TreeScanStep.SingleBackStep());
				}
			}
			if (pre.size() > 0) {
				pre.getLast().setRangeLiteral(this.nameForLastStep);
			}
			return pre;
		}

	}

	static final class SingleBackStep extends TreeScanStep {
		SingleBackStep(String raw, String rangeLiteral) {
			super(raw, rangeLiteral);
		}

		public SingleBackStep() {
			this("..~", null);
		}

	}


	static final class SingleSideStep extends TreeScanStep {

		private Integer indexManipulation;

		SingleSideStep(String raw, String rangeLiteral, Integer indexManipulation) {
			super(raw, rangeLiteral);
			this.indexManipulation = indexManipulation;
		}

		public Integer getIndexManipulation() {
			return indexManipulation;
		}
	}


	static final class LevelScan extends TreeScanStep {

		private ScanBoundaries boundaries;

		public LevelScan(String raw, String rangeLiteral, ScanBoundaries boundaries) {
			super(raw, rangeLiteral);
			this.boundaries = boundaries;
		}

		@Override
		public ScanBoundaries getBoundaries() {
			return boundaries;
		}

		@Override
		public boolean ranged() {
			return true;
		}

		public List<List<TreeScanStep>> interpolate(NavigableTreeNode contextNode) {
			if (contextNode.getSelfIndex().isEmpty()) {
				// this is bad introduce new ImpossibleStep
				return List.of(List.of());
			}
			Integer index = contextNode.getSelfIndex().get();
			Integer size = contextNode.getPreviousNode().orElseThrow().getSize()
					.orElseThrow(() -> new IllegalStateException());
			List<List<TreeScanStep>> result = new LinkedList<>();
			int fromIndexOffset;
			int toIndexOffset;
			if (boundaries.startInclusive == null) {
				fromIndexOffset = -index;
			} else {
				fromIndexOffset = Math.max(boundaries.startInclusive, -index);
			}
			if (boundaries.endInclusive == null) {
				toIndexOffset = (size - index) - 1;
			} else {
				toIndexOffset = Math.min(boundaries.endInclusive, (size - index) - 1);
			}
			if (fromIndexOffset == toIndexOffset) {
				result.add(List.of(toSingleSteps(fromIndexOffset)));
			}
			if (fromIndexOffset > toIndexOffset) {
				for (int i = fromIndexOffset; i >= toIndexOffset; i--) {
					result.add(List.of(toSingleSteps(i)));
				}
			}
			if (fromIndexOffset < toIndexOffset) {
				for (int i = fromIndexOffset; i <= toIndexOffset; i++) {
					result.add(List.of(toSingleSteps(i)));
				}
			}
			result.get(0).get(0).setRangeLiteral(getRangeLiteral().orElse(null));
			return result;
		}

		private TreeScanStep toSingleSteps(int offset) {
			return new TreeScanStep.SingleSideStep("[" + offset + "]~" + getRangeLiteral().orElse(""), null, offset);
		}
	}

	public static class ScanBoundaries {

		Integer startInclusive;

		Integer endInclusive;

		public ScanBoundaries(String startInclusive, String endInclusive, String fullStepExpression) {
			try {
				this.startInclusive = Integer.parseInt(startInclusive);
			} catch (Exception e) {
				// ignore
			}
			try {
				this.endInclusive = Integer.parseInt(endInclusive);
			} catch (Exception e) {
				// ignore
			}
			if ((this.startInclusive == null || this.endInclusive == null)) {
				throw new IllegalArgumentException(String.format("'%s' and '%s' are not valid boundaries in '%s'",
						startInclusive, endInclusive, fullStepExpression));
			}
		}

		public Integer getStartInclusive() {
			return startInclusive;
		}

		public Integer getEndInclusive() {
			return endInclusive;
		}
	}

}
