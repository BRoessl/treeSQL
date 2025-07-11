package io.broessl.treesql.core.eval.stack;

import io.broessl.testutils.TestWithJsonData;
import io.broessl.treesql.core.ScannableTreeNode;
import io.broessl.treesql.core.types.*;
import io.broessl.treesql.json.NavigableJsonNode;
import io.broessl.treesql.sql.ExpressionParser;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StackEvaluationTest {

  @Test
  void testEvaluateNumbers() {
    List<Stackable> expressionStack = ExpressionParser.parseExpressionStack("1 + 1");
    var result = new StackEvaluation(expressionStack).evaluate(null);
    Assertions.assertEquals(new TreeNumber(2), result);
  }

  @Test
  void testEvaluateStrings() {
    List<Stackable> expressionStack = ExpressionParser.parseExpressionStack("'FOO' + 'BAR'");
    var result = new StackEvaluation(expressionStack).evaluate(null);
    Assertions.assertEquals(new TreeString("FOOBAR"), result);
  }

  @Test
  void testEvaluateFunction() {
    List<Stackable> expressionStack = ExpressionParser.parseExpressionStack("MAX(1, 2)");
    var result = new StackEvaluation(expressionStack).evaluate(null);
    Assertions.assertEquals(new TreeNumber(2), result);
  }

  @Test
  void testEvaluateEquals() {
    List<Stackable> expressionStack = ExpressionParser.parseExpressionStack("MAX(1, 2) == 1 + 1");
    var result = new StackEvaluation(expressionStack).evaluate(null);
    Assertions.assertEquals(new TreeBool(true), result);
  }

  @Test
  void testEvaluateComplexA() {
    List<Stackable> expressionStack = ExpressionParser.parseExpressionStack("1 + 2 * 3 - 4");
    var result = new StackEvaluation(expressionStack).evaluate(null);
    Assertions.assertEquals(new TreeNumber(3), result);
  }

  @Test
  void testEvaluateComplexB() {
    List<Stackable> expressionStack = ExpressionParser.parseExpressionStack("1 + 2 * (3 - 4)");
    var result = new StackEvaluation(expressionStack).evaluate(null);
    Assertions.assertEquals(new TreeNumber(-1), result);
  }

  @Test
  void testEvaluateComplexC() {
    List<Stackable> expressionStack =
        ExpressionParser.parseExpressionStack("SIN((1 + 2) * (3 - 4))"); // sin of -3
    // is
    // -0.1411200081
    var result = new StackEvaluation(expressionStack).evaluate(null);
    Assertions.assertEquals(
        -0.1411200081, ((TreeNumber) result).getValue().doubleValue(), 0.0000000001);
  }

  @Test
  void testEvaluateWithContext() {
    ScannableTreeNode patternScan =
        ScannableTreeNode.forRoot(
            NavigableJsonNode.linkRoot(TestWithJsonData.testDataSimpleDataTree()));
    List<ScannableTreeNode> scanResult = patternScan.scan("/~level_1/~level_2/~level_3").toList();
    Assertions.assertEquals(1, scanResult.size());
    ScannableTreeNode node = scanResult.get(0);

    List<Stackable> expressionStackA =
        ExpressionParser.parseExpressionStack("'FOO' + level_1 + level_2 + level_3");
    var resultA = new StackEvaluation(expressionStackA).evaluate(node);
    Assertions.assertEquals("FOOhighlynestedobjects", ((TreeString) resultA).getValue());

    List<Stackable> expressionStackB = ExpressionParser.parseExpressionStack("@level_3");
    var resultB = new StackEvaluation(expressionStackB).evaluate(node);
    Assertions.assertEquals(true, ((TreeBool) resultB).getValue());

    List<Stackable> expressionStackC = ExpressionParser.parseExpressionStack("~level_2");
    var resultC = new StackEvaluation(expressionStackC).evaluate(node);
    Assertions.assertEquals("/highly/nested", ((TreeString) resultC).getValue());

    List<Stackable> expressionStackD =
        ExpressionParser.parseExpressionStack("~level_2 == '/highly/nested'");
    var resultD = new StackEvaluation(expressionStackD).evaluate(node);
    Assertions.assertEquals(true, ((TreeBool) resultD).getValue());
  }
}
