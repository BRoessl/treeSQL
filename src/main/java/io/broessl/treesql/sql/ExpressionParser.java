package io.broessl.treesql.sql;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import io.broessl.treesql.grammar.TreeSQLLexer;
import io.broessl.treesql.grammar.TreeSQLParser;
import io.broessl.treesql.grammar.TreeSQLParser.ExprContext;
import io.broessl.treesql.core.eval.OperationRegistry;
import io.broessl.treesql.core.eval.StackOperation;
import io.broessl.treesql.core.eval.stack.Stackable;
import io.broessl.treesql.core.types.TreeValue;

public final class ExpressionParser implements ParseTreeListener {

    public static List<Stackable> parseExpressionStack(String rawExpression) {
        CodePointCharStream charStream = CharStreams.fromString(rawExpression);
        TreeSQLLexer lexer = new TreeSQLLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        TreeSQLParser parser = new TreeSQLParser(tokenStream);
        ExprContext expr = parser.expr();
        return parseExpressionStack(expr);
    }

    public static List<Stackable> parseExpressionStack(ExprContext expr) {
        ExpressionParser exprParser = new ExpressionParser(expr);
        new ParseTreeWalker().walk(exprParser, expr);
        return exprParser.getStack();
    }

    private ExpressionParser(ExprContext expr) {
        this.expr = expr;
    }

    ExprContext expr;

    private List<Stackable> output = new ArrayList<>();

    private List<TerminalNode> operatorStack = new ArrayList<>();

    private static final List<Integer> LITERAL_TYPES = List.of(TreeSQLLexer.NUMERIC_LITERAL,
            TreeSQLLexer.STRING_LITERAL, TreeSQLLexer.NULL,
            TreeSQLLexer.TRUE, TreeSQLLexer.FALSE,
            TreeSQLLexer.RANGED_LITERAL,
            TreeSQLLexer.JSON_TEXT_VALUE, TreeSQLLexer.VALUE_AT_LITERAL, TreeSQLLexer.PATH_VARIABLE);

    public List<Stackable> getStack() {
        while (!operatorStack.isEmpty()) {
            TerminalNode topOperator = operatorStack.removeLast();
            StackOperation operation = OperationRegistry.getInstance().getOperation(topOperator.getText());
            output.add(operation);
        }
        if (output.isEmpty()) {
            throw new IllegalArgumentException("No valid expression parsed for: " + expr.getText());
        }
        return output;
    }

    private void literalNodeToOutput(TerminalNode literal) {
        TreeValue treeLiteral = parseLiteral(literal);
        output.add(treeLiteral);
    }

    private TreeValue parseLiteral(TerminalNode literal) {
        switch (literal.getSymbol().getType()) {
            case TreeSQLLexer.NUMERIC_LITERAL:
                return TreeValue.parseNumber(literal.getText());
            case TreeSQLLexer.STRING_LITERAL:
                return TreeValue.parseString(literal.getText());
            case TreeSQLLexer.NULL:
                return TreeValue.parseNull(literal.getText());
            case TreeSQLLexer.TRUE:
                return TreeValue.parseBoolean(literal.getText());
            case TreeSQLLexer.FALSE:
                return TreeValue.parseBoolean(literal.getText());
            case TreeSQLLexer.RANGED_LITERAL:
                return TreeValue.parseRangedLiteral(literal.getText());
            case TreeSQLLexer.JSON_TEXT_VALUE:
                return TreeValue.parseRangedJSONPointer(literal.getText());
            case TreeSQLLexer.VALUE_AT_LITERAL:
                return TreeValue.parseValueAt(literal.getText());
            case TreeSQLLexer.PATH_VARIABLE:
                return TreeValue.parsePathVariable(literal.getText());
            default:
                break;
        }
        throw new IllegalArgumentException(
                "Unknown literal type: " + literal.getSymbol().getType() + " with text: " + literal.getText());
    }

    private void operatorNodeToOutput(TerminalNode operator) {
        StackOperation operation = OperationRegistry.getInstance().getOperation(operator.getText());
        output.add(operation);
    }

    private static boolean isLiteral(TerminalNode node) {
        return LITERAL_TYPES.contains(node.getSymbol().getType());
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        if (isLiteral(node)) {
            literalNodeToOutput(node);
        } else if (node.getSymbol().getType() == TreeSQLLexer.FUNC_NAME) {
            operatorStack.add(node);
        } else if (OperatorPrecedence.contains(node.getSymbol().getType())) {
            var op1 = OperatorPrecedence.get(node.getSymbol().getType());
            while (!operatorStack.isEmpty()
                    && operatorStack.getLast().getSymbol().getType() != TreeSQLLexer.OPEN_PAR) {
                var op2 = OperatorPrecedence.get(operatorStack.getLast().getSymbol().getType());
                if (op2.precedence >= op1.precedence) {
                    operatorNodeToOutput(operatorStack.removeLast());
                    continue;
                }
                break;
            }
            operatorStack.addLast(node);
        } else if (node.getSymbol().getType() == TreeSQLLexer.COMMA) {
            while (operatorStack.getLast().getSymbol().getType() != TreeSQLLexer.OPEN_PAR) {
                operatorNodeToOutput(operatorStack.removeLast());
            }
        } else if (node.getSymbol().getType() == TreeSQLLexer.OPEN_PAR) {
            operatorStack.addLast(node);

        } else if (node.getSymbol().getType() == TreeSQLLexer.CLOSE_PAR) {
            while (operatorStack.getLast().getSymbol().getType() != TreeSQLLexer.OPEN_PAR) {
                operatorNodeToOutput(operatorStack.removeLast());
            }
            operatorStack.removeLast();
            if (operatorStack.getLast().getSymbol().getType() == TreeSQLLexer.FUNC_NAME) {
                operatorNodeToOutput(operatorStack.removeLast());
            }
        }
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        throw new IllegalArgumentException("Unable to parse <" + node.toString() + "> at "
                + node.getSymbol().getCharPositionInLine() + ".\n" +
                "Please check your syntax: Strings must be enclosed in single quotes, ranged JSON pointers must be enclosed in double quotes.");
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
    }

}