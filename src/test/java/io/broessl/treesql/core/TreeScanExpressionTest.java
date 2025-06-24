package io.broessl.treesql.core;

import com.fasterxml.jackson.databind.JsonNode;
import io.broessl.testutils.TestWithJsonData;
import io.broessl.treesql.core.TreeScanStep.*;
import io.broessl.treesql.json.NavigableJsonNode;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class TreeScanExpressionTest extends TestWithJsonData {

  @ParameterizedTest
  @CsvFileSource(resources = "/test_csv/legalExpressions.csv", numLinesToSkip = 1, delimiter = ';')
  void testLegalExpresions(String expression, String parts) {
    Assertions.assertEquals(
        Integer.parseInt(parts), TreeScanExpression.parse(expression).steps().size());
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/test_csv/illegalExpressions.csv", numLinesToSkip = 1)
  void testIllegalExpresions(String expression, String reason) {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          TreeScanExpression.parse(expression);
        },
        reason);
  }

  @Test
  void testParseBackwardStepsExpresions() {
    TreeScanExpression expression =
        TreeScanExpression.parse("/..~/{-2,-2}~/{-1,-3}~/{0,-3}~/{-1,-99}~/..~myBinding");
    List<TreeScanStep> step = expression.steps();
    Assertions.assertEquals(6, step.size());
    Assertions.assertInstanceOf(TreeScanStep.SingleBackStep.class, step.get(0));
    DepthScan i1 = Assertions.assertInstanceOf(TreeScanStep.DepthScan.class, step.get(1));
    DepthScan i2 = Assertions.assertInstanceOf(TreeScanStep.DepthScan.class, step.get(2));
    DepthScan i3 = Assertions.assertInstanceOf(TreeScanStep.DepthScan.class, step.get(3));
    DepthScan i4 = Assertions.assertInstanceOf(TreeScanStep.DepthScan.class, step.get(4));
    SingleBackStep i5 = Assertions.assertInstanceOf(TreeScanStep.SingleBackStep.class, step.get(5));
    Assertions.assertTrue(i1.getRangeLiteral().isEmpty());
    Assertions.assertEquals(-2, i1.getBoundaries().getStartInclusive());
    Assertions.assertEquals(-2, i1.getBoundaries().getEndInclusive());
    Assertions.assertEquals(-1, i2.getBoundaries().getStartInclusive());
    Assertions.assertEquals(-3, i2.getBoundaries().getEndInclusive());
    Assertions.assertEquals(0, i3.getBoundaries().getStartInclusive());
    Assertions.assertEquals(-3, i3.getBoundaries().getEndInclusive());
    Assertions.assertEquals(-1, i4.getBoundaries().getStartInclusive());
    Assertions.assertEquals(-99, i4.getBoundaries().getEndInclusive());
    Assertions.assertEquals("myBinding", i5.getRangeLiteral().get());
  }

  @Test
  void testParseForwardStepsExpresions() {
    TreeScanExpression expression =
        TreeScanExpression.parse("/~/{2,2}~/{1,3}~/{0,3}~/{1,5}~/~myBinding");
    List<TreeScanStep> step = expression.steps();
    Assertions.assertEquals(6, step.size());
    Assertions.assertInstanceOf(TreeScanStep.SingleForwardStep.class, step.get(0));
    DepthScan i1 = Assertions.assertInstanceOf(TreeScanStep.DepthScan.class, step.get(1));
    DepthScan i2 = Assertions.assertInstanceOf(TreeScanStep.DepthScan.class, step.get(2));
    DepthScan i3 = Assertions.assertInstanceOf(TreeScanStep.DepthScan.class, step.get(3));
    DepthScan i4 = Assertions.assertInstanceOf(TreeScanStep.DepthScan.class, step.get(4));
    SingleForwardStep i5 =
        Assertions.assertInstanceOf(TreeScanStep.SingleForwardStep.class, step.get(5));
    Assertions.assertTrue(i1.getRangeLiteral().isEmpty());
    Assertions.assertEquals(2, i1.getBoundaries().getStartInclusive());
    Assertions.assertEquals(2, i1.getBoundaries().getEndInclusive());
    Assertions.assertEquals(1, i2.getBoundaries().getStartInclusive());
    Assertions.assertEquals(3, i2.getBoundaries().getEndInclusive());
    Assertions.assertEquals(0, i3.getBoundaries().getStartInclusive());
    Assertions.assertEquals(3, i3.getBoundaries().getEndInclusive());
    Assertions.assertEquals(1, i4.getBoundaries().getStartInclusive());
    Assertions.assertEquals(5, i4.getBoundaries().getEndInclusive());
    Assertions.assertEquals("myBinding", i5.getRangeLiteral().get());
  }

  @Test
  void testParseSideStepsExpresions() {
    TreeScanExpression expression =
        TreeScanExpression.parse("/[+1]~/[-2]~/[+1,+3]~/[0,+3]~/[+1,+99]~/[-99,+99]~myBinding");
    List<TreeScanStep> step = expression.steps();
    Assertions.assertEquals(6, step.size());
    SingleSideStep i0 = Assertions.assertInstanceOf(TreeScanStep.SingleSideStep.class, step.get(0));
    SingleSideStep i1 = Assertions.assertInstanceOf(TreeScanStep.SingleSideStep.class, step.get(1));
    LevelScan i2 = Assertions.assertInstanceOf(TreeScanStep.LevelScan.class, step.get(2));
    LevelScan i3 = Assertions.assertInstanceOf(TreeScanStep.LevelScan.class, step.get(3));
    LevelScan i4 = Assertions.assertInstanceOf(TreeScanStep.LevelScan.class, step.get(4));
    LevelScan i5 = Assertions.assertInstanceOf(TreeScanStep.LevelScan.class, step.get(5));
    Assertions.assertTrue(i1.getRangeLiteral().isEmpty());
    Assertions.assertEquals(1, i0.getIndexManipulation());
    Assertions.assertEquals(-2, i1.getIndexManipulation());
    Assertions.assertEquals(1, i2.getBoundaries().getStartInclusive());
    Assertions.assertEquals(3, i2.getBoundaries().getEndInclusive());
    Assertions.assertEquals(0, i3.getBoundaries().getStartInclusive());
    Assertions.assertEquals(3, i3.getBoundaries().getEndInclusive());
    Assertions.assertEquals(1, i4.getBoundaries().getStartInclusive());
    Assertions.assertEquals(99, i4.getBoundaries().getEndInclusive());
    Assertions.assertEquals("myBinding", i5.getRangeLiteral().get());
    Assertions.assertEquals(-99, i5.getBoundaries().getStartInclusive());
    Assertions.assertEquals(99, i5.getBoundaries().getEndInclusive());
  }

  @Test
  void testInterpolate() {
    {
      TreeScanExpression expression = TreeScanExpression.parse("/{1,3}~/foobar");
      List<TreeScanExpression> interpolateExpressions = expression.interpolateExpression(null);
      Assertions.assertEquals(3, interpolateExpressions.size());
      Assertions.assertEquals("/~/foobar", interpolateExpressions.get(0).toString());
      Assertions.assertEquals("/~/~/foobar", interpolateExpressions.get(1).toString());
      Assertions.assertEquals("/~/~/~/foobar", interpolateExpressions.get(2).toString());
    }
    {
      TreeScanExpression expression = TreeScanExpression.parse("/{3,1}~/foobar");
      List<TreeScanExpression> interpolateExpressions = expression.interpolateExpression(null);
      Assertions.assertEquals(3, interpolateExpressions.size());
      Assertions.assertEquals("/~/~/~/foobar", interpolateExpressions.get(0).toString());
      Assertions.assertEquals("/~/~/foobar", interpolateExpressions.get(1).toString());
      Assertions.assertEquals("/~/foobar", interpolateExpressions.get(2).toString());
    }

    {
      TreeScanExpression expression = TreeScanExpression.parse("/{-1,-3}~/foobar");
      List<TreeScanExpression> interpolateExpressions = expression.interpolateExpression(null);
      Assertions.assertEquals(3, interpolateExpressions.size());
      Assertions.assertEquals("/..~/foobar", interpolateExpressions.get(0).toString());
      Assertions.assertEquals("/..~/..~/foobar", interpolateExpressions.get(1).toString());
      Assertions.assertEquals("/..~/..~/..~/foobar", interpolateExpressions.get(2).toString());
    }
    {
      TreeScanExpression expression = TreeScanExpression.parse("/{-0,-1}~/foobar");
      List<TreeScanExpression> interpolateExpressions = expression.interpolateExpression(null);
      Assertions.assertEquals(2, interpolateExpressions.size());
      Assertions.assertEquals("/foobar", interpolateExpressions.get(0).toString());
      Assertions.assertEquals("/..~/foobar", interpolateExpressions.get(1).toString());
    }
    {
      JsonNode testDataArrayWithTenIntegers = testDataArrayWithTenIntegers();
      NavigableJsonNode backrefNode =
          NavigableJsonNode.tryLink(
                  testDataArrayWithTenIntegers, testDataArrayWithTenIntegers.get(5))
              .get();
      TreeScanExpression expression = TreeScanExpression.parse("/[-1]~/foobar");
      List<TreeScanExpression> interpolateExpressions =
          expression.interpolateExpression(backrefNode);
      Assertions.assertEquals(1, interpolateExpressions.size());
      Assertions.assertEquals("/[-1]~/foobar", interpolateExpressions.get(0).toString());
    }
    {
      JsonNode testDataArrayWithTenIntegers = testDataArrayWithTenIntegers();
      NavigableJsonNode backrefNode =
          NavigableJsonNode.tryLink(
                  testDataArrayWithTenIntegers, testDataArrayWithTenIntegers.get(7))
              .get();
      TreeScanExpression expression = TreeScanExpression.parse("/[0,+99]~/foobar");
      List<TreeScanExpression> interpolateExpressions =
          expression.interpolateExpression(backrefNode);
      Assertions.assertEquals(3, interpolateExpressions.size());
      Assertions.assertEquals("/[0]~/foobar", interpolateExpressions.get(0).toString());
      Assertions.assertEquals("/[1]~/foobar", interpolateExpressions.get(1).toString());
      Assertions.assertEquals("/[2]~/foobar", interpolateExpressions.get(2).toString());
    }
    {
      JsonNode testDataArrayWithTenIntegers = testDataArrayWithTenIntegers();
      NavigableJsonNode backrefNode =
          NavigableJsonNode.tryLink(
                  testDataArrayWithTenIntegers, testDataArrayWithTenIntegers.get(1))
              .get();
      TreeScanExpression expression = TreeScanExpression.parse("/[-99,0]~/foobar");
      List<TreeScanExpression> interpolateExpressions =
          expression.interpolateExpression(backrefNode);
      Assertions.assertEquals(2, interpolateExpressions.size());
      Assertions.assertEquals("/[-1]~/foobar", interpolateExpressions.get(0).toString());
      Assertions.assertEquals("/[0]~/foobar", interpolateExpressions.get(1).toString());
    }
  }

  @Test
  void testInterpolateSideReversed() {
    JsonNode testDataArrayWithTenIntegers = testDataArrayWithTenIntegers();
    NavigableJsonNode backrefNode =
        NavigableJsonNode.tryLink(testDataArrayWithTenIntegers, testDataArrayWithTenIntegers.get(5))
            .get();
    TreeScanExpression expression = TreeScanExpression.parse("/[+1,-1]~/foobar");
    List<TreeScanExpression> interpolateExpressions = expression.interpolateExpression(backrefNode);
    Assertions.assertEquals(3, interpolateExpressions.size());
    Assertions.assertEquals("/[1]~/foobar", interpolateExpressions.get(0).toString());
    Assertions.assertEquals("/[0]~/foobar", interpolateExpressions.get(1).toString());
    Assertions.assertEquals("/[-1]~/foobar", interpolateExpressions.get(2).toString());
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/test_csv/literalsOnly.csv", numLinesToSkip = 1)
  void testToLiteralsOnly(String source, String result, String localName) {
    TreeScanExpression expression = TreeScanExpression.parse(source);
    expression.toLiteralsOnly();
    Assertions.assertEquals(result, expression.toString());
    Assertions.assertEquals(localName, expression.getLocalNameBinding());
  }

  @Test
  void testToLiteralsOnlyFailures() {
    TreeScanExpression expression = TreeScanExpression.parse("/{1,3}~/a");
    Assertions.assertTrue(expression.hasRangedSteps());
    Assertions.assertThrows(IllegalArgumentException.class, () -> expression.toLiteralsOnly());
  }
}
