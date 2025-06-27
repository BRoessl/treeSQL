package io.broessl.treesql.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TreeScanExpression {

  private List<TreeScanStep> steps;

  public TreeScanExpression(List<TreeScanStep> steps) {
    this.steps = steps;
  }

  public TreeScanStep currentStep() {
    return steps.getFirst();
  }

  public TreeScanStep lastStep() {
    return steps.getLast();
  }

  public TreeScanExpression subExpression() {
    List<TreeScanStep> subExpression = new LinkedList<>(steps);
    subExpression.removeFirst();
    return new TreeScanExpression(subExpression);
  }

  public boolean matches() {
    return steps.isEmpty();
  }

  /**
   * expressions, where the next step is ranged, must be interpolated to a list of expressions with
   * non-ranged next steps.
   *
   * @param contextNode
   * @return
   */
  public List<TreeScanExpression> interpolateExpression(NavigableTreeNode contextNode) {
    LinkedList<TreeScanExpression> result = new LinkedList<>();
    if (this.matches() || !steps.getFirst().interpolatable()) {
      result.add(this);
      return result;
    }
    TreeScanStep forInterpolation = steps.getFirst();
    List<List<TreeScanStep>> allInpolations =
        switch (forInterpolation) {
          case TreeScanStep.DepthScan bs -> bs.interpolate();
          case TreeScanStep.LevelScan sd -> sd.interpolate(contextNode);
          case TreeScanStep.RegexStep rs -> rs.interpolate(contextNode);
          default -> throw new IllegalStateException();
        };
    for (List<TreeScanStep> interpolatedSteps : allInpolations) {
      TreeScanExpression subExpression = subExpression();
      result.add(subExpression.prepend(interpolatedSteps));
    }
    return result;
  }

  public static TreeScanExpression parse(String expression) {
    if (expression == null || expression.isEmpty()) {
      return new TreeScanExpression(List.of());
    }
    if (expression.charAt(0) != '/') {
      throw new IllegalArgumentException("scan expression must start with '/'");
    }
    List<String> pathParts = split(expression);

    List<TreeScanStep> steps = new LinkedList<>();
    for (String mightBeEscaped : pathParts) {
      steps.add(createStepFrom(mightBeEscaped));
    }
    return new TreeScanExpression(steps);
  }

  private static TreeScanStep createStepFrom(String rawUnescaped) {
    return TreeScanStep.pattern(
        rawUnescaped,
        s -> {
          if (s.matches(".*~[^01].*")) {
            throw new IllegalArgumentException(
                String.format(
                    "Step '%s' is invalid. '~' must be escaped with '~0' or used as named/unnamed range literal.",
                    rawUnescaped));
          }
          return TreeScanStep.literal(replaceEscapedLiterals(rawUnescaped));
        });
  }

  private static List<String> split(String expression) {
    if (!expression.startsWith("/")) {
      throw new IllegalArgumentException();
    }
    List<Integer> splitIndices = new ArrayList<>();
    for (int i = 0; i < expression.length(); i++) {
      if (expression.charAt(i) == '/') {
        splitIndices.add(i);
      }
    }
    Integer previousIndex = null;
    List<String> pathParts = new ArrayList<>();
    for (Integer splitHere : splitIndices) {
      if (previousIndex == null) {
        previousIndex = splitHere;
        continue;
      }
      String pathPart = expression.substring(previousIndex + 1, splitHere);
      pathParts.add(pathPart);
      previousIndex = splitHere;
    }
    // add last part
    pathParts.add(expression.substring(previousIndex + 1));
    return pathParts;
  }

  private static String replaceEscapedLiterals(String escaped) {
    return escaped.replace("~1", "/").replace("~0", "~");
  }

  public List<TreeScanStep> steps() {
    return steps;
  }

  public TreeScanExpression prepend(List<TreeScanStep> toPrepend) {
    List<TreeScanStep> combined = new LinkedList<>(toPrepend);
    combined.addAll(steps);
    return new TreeScanExpression(combined);
  }

  @Override
  public String toString() {
    return steps.stream().map(s -> "/" + s.toString()).collect(Collectors.joining());
  }

  public boolean hasRangedSteps() {
    return steps.stream().filter(s -> s.interpolatable()).count() != 0;
  }

  public boolean literalsOnly() {
    return steps.stream().filter(s -> !s.isLiteral()).count() == 0;
  }

  public void toLiteralsOnly() {
    if (hasRangedSteps()) {
      throw new IllegalArgumentException(
          "expression contains ranged steps, can not create expression with literals only");
    }
    List<TreeScanStep> reversedStepsWithBackSteps = this.steps().reversed();
    Iterator<TreeScanStep> backStepIterator = reversedStepsWithBackSteps.iterator();
    int remove = 0;
    while (backStepIterator.hasNext()) {
      TreeScanStep next = backStepIterator.next();
      if (next instanceof TreeScanStep.SingleSideStep && remove > 0) {
        backStepIterator.remove();
        continue;
      }
      if (next.isLiteral() && remove > 0) {
        backStepIterator.remove();
        remove--;
        continue;
      }
      if (next instanceof TreeScanStep.SingleBackStep) {
        backStepIterator.remove();
        remove++;
        continue;
      }
    }
    List<TreeScanStep> reversedStepsWithSideSteps = this.steps().reversed();
    Iterator<TreeScanStep> sideStepIterator = reversedStepsWithSideSteps.iterator();
    boolean doOffset = false;
    int offset = 0;
    while (sideStepIterator.hasNext()) {
      TreeScanStep next = sideStepIterator.next();
      if (next instanceof TreeScanStep.SingleSideStep sideStep && !doOffset) {
        doOffset = true;
        offset = sideStep.getIndexManipulation();
        sideStepIterator.remove();
        continue;
      }
      if (next instanceof TreeScanStep.SingleSideStep sideStep && doOffset) {
        offset = offset + sideStep.getIndexManipulation();
        sideStepIterator.remove();
        continue;
      }
      if (next instanceof TreeScanStep.LiteralForwardStep literalStep && doOffset) {
        literalStep.updateWithOffset(offset);
        doOffset = false;
        continue;
      }
    }
    if (!literalsOnly()) {
      throw new IllegalStateException();
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.toString());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TreeScanExpression other = (TreeScanExpression) obj;
    return Objects.equals(this.toString(), other.toString());
  }

  public String getLocalNameBinding() {
    if (literalsOnly()) {
      return steps.getLast().getRaw();
    }
    throw new IllegalArgumentException("can only be called on literal only expressions");
  }
}
