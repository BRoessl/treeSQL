package io.broessl.treesql.sql;

import io.broessl.treesql.grammar.TreeSQLLexer;
import java.util.ArrayList;
import java.util.List;

public class OperatorPrecedence {

  private static final List<OperatorPrecedence> operators = new ArrayList<>();

  public static boolean contains(int type) {
    for (OperatorPrecedence operator : operators) {
      if (operator.type == type) {
        return true;
      }
    }
    return false;
  }

  public static OperatorPrecedence get(int tokenIndex) {
    for (OperatorPrecedence operator : operators) {
      if (operator.type == tokenIndex) {
        return operator;
      }
    }
    throw new IllegalArgumentException("no operator for " + tokenIndex);
  }

  static {
    operators.add(create(TreeSQLLexer.STAR, 5));
    operators.add(create(TreeSQLLexer.DIV, 5));
    operators.add(create(TreeSQLLexer.MOD, 5));
    operators.add(create(TreeSQLLexer.PLUS, 4));
    operators.add(create(TreeSQLLexer.MINUS, 4));
    operators.add(create(TreeSQLLexer.LT_EQ, 2));
    operators.add(create(TreeSQLLexer.GT_EQ, 2));
    operators.add(create(TreeSQLLexer.EQ, 2));
    operators.add(create(TreeSQLLexer.NOT_EQ, 2));
    operators.add(create(TreeSQLLexer.LT, 2));
    operators.add(create(TreeSQLLexer.GT, 2));
    operators.add(create(TreeSQLLexer.MATCH, 2));
    operators.add(create(TreeSQLLexer.NOT_MATCH, 2));
    operators.add(create(TreeSQLLexer.IN, 2));
    operators.add(create(TreeSQLLexer.NOT_IN, 2));
    operators.add(create(TreeSQLLexer.NOT, 1));
    operators.add(create(TreeSQLLexer.AND, 0));
    operators.add(create(TreeSQLLexer.OR, 0));
  }

  public final int type;

  public final int precedence;

  private OperatorPrecedence(int type, int precedence) {
    this.type = type;
    this.precedence = precedence;
  }

  public static OperatorPrecedence create(int type, int precedence) {
    return new OperatorPrecedence(type, precedence);
  }
}
