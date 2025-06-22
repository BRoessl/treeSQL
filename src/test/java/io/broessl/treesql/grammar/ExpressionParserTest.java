package io.broessl.treesql.grammar;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.broessl.treesql.core.eval.stack.Stackable;
import io.broessl.treesql.sql.ExpressionParser;

public class ExpressionParserTest {

    @Test
    void testShuntingYardAlgoExampleA() {
        var stack = ExpressionParser.parseExpressionStack("1 + 2 * 3 - 4");
        Assertions.assertEquals("1, 2, 3, *, +, 4, -", stack.stream().map(Stackable::toString).collect(Collectors.joining(", ")));
    }

    @Test
    void testShuntingYardAlgoExampleB() {
        var stack = ExpressionParser.parseExpressionStack("SIN(MAX(2, 3) / 3 * 4)");
        Assertions.assertEquals("2, 3, MAX, 3, /, 4, *, SIN", stack.stream().map(Stackable::toString).collect(Collectors.joining(", ")));
    }

}
