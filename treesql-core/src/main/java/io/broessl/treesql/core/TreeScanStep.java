package io.broessl.treesql.core;

import io.broessl.treesql.core.types.TreeNumber;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract sealed class TreeScanStep {

  private static final Pattern ANON_FORWARD = Pattern.compile("^~$");
  private static final Pattern NAMED_FORWARD = Pattern.compile("^~([a-z]\\w+)$");

  private static final Pattern ANON_BACK = Pattern.compile("^~\\.\\.$");
  private static final Pattern NAMED_BACK = Pattern.compile("^~([a-z][a-z0-9_]*)\\.\\.$");

  private static final Pattern ANON_SIBLING = Pattern.compile("^~\\[(0|[+-][\\d]+)\\]$");
  private static final Pattern NAMED_SIBLING = Pattern.compile("^~([a-z][a-z0-9_]*)\\[(0|[+-][\\d]+)\\]$");

  private static final Pattern ANON_DEPTH_RANGED_SCAN = Pattern.compile("^~\\{(-?\\d+),(-?\\d+)\\}$");
  private static final Pattern NAMED_DEPTH_RANGED_SCAN = Pattern.compile("^~([a-z][a-z0-9_]*)\\{(-?\\d+),(-?\\d+)\\}$");

  private static final Pattern ANON_DEPTH_SCAN = Pattern.compile("~^\\{(-?\\d+)\\}$");
  private static final Pattern NAMED_DEPTH_SCAN = Pattern.compile("^~([a-z][a-z0-9_]*)\\{(-?\\d+)\\}$");

  private static final Pattern ANON_LEVEL_SCAN = Pattern.compile("^~\\[(0|[+-][\\d]+),(0|[+-][\\d]+)\\]$");
  private static final Pattern NAMED_LEVEL_SCAN = Pattern
      .compile("^~([a-z][a-z0-9_]*)\\[(0|[+-][\\d]+),(0|[+-][\\d]+)\\]$");

  private static final Pattern ANON_REGEX_CHILDREN_SCAN = Pattern.compile("^~(\\(.*\\))$");
  private static final Pattern NAMED_REGEX_CHILDREN_SCAN = Pattern.compile("^~([a-z][a-z0-9_]*)(\\(.*\\))$");

  private static final Pattern SPECIAL_DIRECTIVE = Pattern.compile("^~([A-Z]\\w+)(?:\\((.*)\\))?$", Pattern.DOTALL);

  private String raw;

  private Optional<String> rangeLiteral;

  public final Optional<String> getRangeLiteral() {
    return rangeLiteral;
  }

  protected void setRangeLiteral(String rangeLiteral) {
    this.rangeLiteral = rangeLiteral == null || rangeLiteral.isBlank()
        ? Optional.empty()
        : Optional.of(rangeLiteral);
  }

  public final String getRaw() {
    return raw;
  }

  public boolean isLiteral() {
    return false;
  }

  public boolean interpolatable() {
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
    this.rangeLiteral = rangeLiteral == null || rangeLiteral.isBlank()
        ? Optional.empty()
        : Optional.of(rangeLiteral);
  }

  public static TreeScanStep literal(String literal) {
    return new LiteralForwardStep(literal);
  }

  public static TreeScanStep matchRangedPattern(
      String raw, Function<String, TreeScanStep> fallback) {
    if (raw.matches("~[a-z].*")) {
      // must be named step
      return namedStep(raw);
    }
    if ("~".equals(raw) || raw.matches("~[^01A-Z].*")) {
      // must be anonymous step
      return anonymousStep(raw);
    }
    // special directive "~MY_DIRECTIVE"
    Matcher matcher = SPECIAL_DIRECTIVE.matcher(raw);
    if (matcher.matches()) {
      return new DirectiveStep("~" + matcher.group(1), matcher.group(2));
    }
    // is not a ranged step, but might be a literal step
    if (fallback == null) {
      throw new IllegalArgumentException("Unknown pattern for '" + raw + "'.");
    } else {
      return fallback.apply(raw);
    }
  }

  private static TreeScanStep namedStep(String raw) {
    Matcher matcher = NAMED_FORWARD.matcher(raw);
    if (matcher.matches()) {
      return new RangedForwardStep(raw, matcher.group(1));
    }
    matcher = NAMED_BACK.matcher(raw);
    if (matcher.matches()) {
      return new SingleBackStep(raw, matcher.group(1));
    }
    matcher = NAMED_DEPTH_RANGED_SCAN.matcher(raw);
    if (matcher.matches()) {
      ScanBoundaries boundaries = new ScanBoundaries(matcher.group(2), matcher.group(3), matcher.group(0));
      return new DepthScan(raw, boundaries, matcher.group(1));
    }
    matcher = NAMED_DEPTH_SCAN.matcher(raw);
    if (matcher.matches()) {
      ScanBoundaries boundaries = new ScanBoundaries(matcher.group(2), matcher.group(2), matcher.group(0));
      return new DepthScan(raw, boundaries, matcher.group(1));
    }
    matcher = NAMED_SIBLING.matcher(raw);
    if (matcher.matches()) {
      ScanBoundaries boundaries = new ScanBoundaries(matcher.group(2), matcher.group(2), matcher.group(0));
      String rangeLiteral = matcher.group(1);
      return new SingleSideStep(raw, rangeLiteral, boundaries.startInclusive);
    }

    matcher = NAMED_REGEX_CHILDREN_SCAN.matcher(raw);
    if (matcher.matches()) {
      String rangeLiteral = matcher.group(1);
      return new RegexStep(raw, rangeLiteral, matcher.group(2));
    }

    matcher = NAMED_LEVEL_SCAN.matcher(raw);
    if (matcher.matches()) {
      ScanBoundaries boundaries = new ScanBoundaries(matcher.group(2), matcher.group(3), matcher.group(0));
      String rangeLiteral = matcher.group(1);
      return new LevelScan(raw, rangeLiteral, boundaries);
    }
    throw new IllegalArgumentException("Unknown pattern for an named ranged step: '" + raw + "'.");
  }

  private static TreeScanStep anonymousStep(String raw) {
    Matcher matcher = ANON_FORWARD.matcher(raw);
    if (matcher.matches()) {
      return new RangedForwardStep(raw, null);
    }
    matcher = ANON_BACK.matcher(raw);
    if (matcher.matches()) {
      return new SingleBackStep(raw, null);
    }

    matcher = ANON_DEPTH_RANGED_SCAN.matcher(raw);
    if (matcher.matches()) {
      ScanBoundaries boundaries = new ScanBoundaries(matcher.group(1), matcher.group(2), matcher.group(0));
      return new DepthScan(raw, boundaries);
    }
    matcher = ANON_DEPTH_SCAN.matcher(raw);
    if (matcher.matches()) {
      ScanBoundaries boundaries = new ScanBoundaries(matcher.group(1), matcher.group(1), matcher.group(0));
      return new DepthScan(raw, boundaries);
    }
    matcher = ANON_SIBLING.matcher(raw);
    if (matcher.matches()) {
      ScanBoundaries boundaries = new ScanBoundaries(matcher.group(1), matcher.group(1), matcher.group(0));
      return new SingleSideStep(raw, null, boundaries.startInclusive);
    }
    matcher = ANON_REGEX_CHILDREN_SCAN.matcher(raw);
    if (matcher.matches()) {
      return new RegexStep(raw, null, matcher.group(1));
    }
    matcher = ANON_LEVEL_SCAN.matcher(raw);
    if (matcher.matches()) {
      ScanBoundaries boundaries = new ScanBoundaries(matcher.group(1), matcher.group(2), matcher.group(0));
      return new LevelScan(raw, null, boundaries);
    }
    throw new IllegalArgumentException(
        "Unknown pattern for an unnamed ranged step: '" + raw + "'.");
  }

  @Override
  public String toString() {
    return raw;
  }

  static final class LiteralForwardStep extends TreeScanStep {
    LiteralForwardStep(String literal) {
      super(literal);
    }

    LiteralForwardStep(String literal, String rangedLiteral) {
      super(literal, rangedLiteral);
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

  static final class RangedForwardStep extends TreeScanStep {

    RangedForwardStep(String raw, String rangeLiteral) {
      super(raw, rangeLiteral);
    }

    RangedForwardStep() {
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
    public boolean interpolatable() {
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
          pre.add(new TreeScanStep.RangedForwardStep());
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

  static final class RegexStep extends TreeScanStep {

    private Pattern pattern;

    RegexStep(String raw, String rangeLiteral, String regex) {
      super(raw, rangeLiteral);
      this.pattern = Pattern.compile(regex);
    }

    public Pattern getPattern() {
      return pattern;
    }

    @Override
    public boolean interpolatable() {
      return true;
    }

    public List<List<TreeScanStep>> interpolate(NavigableTreeNode contextNode) {
      return contextNode
          .children()
          .filter(c -> pattern.matcher(c.getName().getValue().toString()).matches())
          .map(
              c -> {
                TreeScanStep step = new TreeScanStep.LiteralForwardStep(
                    c.getName().getValue().toString(), getRangeLiteral().orElse(null));
                return List.of(step);
              })
          .toList();
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
    public boolean interpolatable() {
      return true;
    }

    public List<List<TreeScanStep>> interpolate(NavigableTreeNode contextNode) {
      Integer index;
      if (contextNode.getName() instanceof TreeNumber idx) {
        index = idx.getValue().intValue();
      } else {
        // this is bad introduce new ImpossibleStep
        return List.of(List.of());
      }

      Integer size = contextNode
          .getParent()
          .orElseThrow()
          .getSize()
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
        result.add(List.of(toSingleSteps(fromIndexOffset, getRangeLiteral().orElse(null))));
      }
      if (fromIndexOffset > toIndexOffset) {
        for (int i = fromIndexOffset; i >= toIndexOffset; i--) {
          result.add(List.of(toSingleSteps(i, getRangeLiteral().orElse(null))));
        }
      }
      if (fromIndexOffset < toIndexOffset) {
        for (int i = fromIndexOffset; i <= toIndexOffset; i++) {
          result.add(List.of(toSingleSteps(i, getRangeLiteral().orElse(null))));
        }
      }
      return result;
    }

    private TreeScanStep toSingleSteps(int offset, String rangeLiteral) {
      return new TreeScanStep.SingleSideStep(
          "[" + offset + "]~" + getRangeLiteral().orElse(""), rangeLiteral, offset);
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
        throw new IllegalArgumentException(
            String.format(
                "'%s' and '%s' are not valid boundaries in '%s'",
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

  static final class DirectiveStep extends TreeScanStep {

    private List<String> arguments;

    DirectiveStep(String directive, String argument) {
      super(directive, null);
      if (argument == null || argument.trim().isEmpty()) {
        arguments = List.of();
      } else {
        this.arguments = argument.lines().toList();
      }
    }

    public List<String> getArguments() {
      return arguments;
    }
  }
}
