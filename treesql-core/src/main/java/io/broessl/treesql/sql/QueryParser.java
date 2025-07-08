package io.broessl.treesql.sql;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.ScannableTreeNode;
import io.broessl.treesql.core.eval.stack.StackEvaluation;
import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeRangedJSONPointer;
import io.broessl.treesql.core.types.TreeValue;
import io.broessl.treesql.grammar.TreeSQLBaseListener;
import io.broessl.treesql.grammar.TreeSQLLexer;
import io.broessl.treesql.grammar.TreeSQLParser;
import io.broessl.treesql.grammar.TreeSQLParser.LimitStmtContext;
import io.broessl.treesql.grammar.TreeSQLParser.OrderByStmtContext;
import io.broessl.treesql.grammar.TreeSQLParser.WhereExprContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class QueryParser extends TreeSQLBaseListener {

  private static QueryParser queryParser;

  public static QueryParser parseStatement(String rawExpression) {
    CodePointCharStream charStream = CharStreams.fromString(rawExpression);
    TreeSQLLexer lexer = new TreeSQLLexer(charStream);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    TreeSQLParser parser = new TreeSQLParser(tokenStream);
    var select = parser.selectStmt();
    queryParser = new QueryParser();
    new ParseTreeWalker().walk(queryParser, select);
    return queryParser;
  }

  public List<String> getColumnNames() {
    return columnNames;
  }

  List<String> columnNames = new ArrayList<>();
  List<StackEvaluation> columnExpressions = new ArrayList<>();
  List<TreeRangedJSONPointer> rangedJsonPointers;
  StackEvaluation whereCondition;
  private Optional<StackEvaluation> ordering = Optional.empty();
  private boolean orderDescending = false;
  private Optional<Integer> limit = Optional.empty();

  private QueryParser() {
    rangedJsonPointers = new ArrayList<>();
    whereCondition = new StackEvaluation(List.of(new TreeBool(true))); // Default to true condition
  }

  private boolean conditionOkay(ScannableTreeNode stn) {
    var evalCondition = whereCondition.evaluate(stn);
    if (evalCondition instanceof TreeBool bool) {
      return bool.getValue();
    }
    throw new IllegalStateException(
        "WHERE condition did not evaluate to a boolean: " + evalCondition);
  }

  public Stream<List<TreePrimitive>> execute(NavigableTreeNode root) {
    if (rangedJsonPointers.isEmpty()) {
      if (conditionOkay(null)) {
        return Stream.of(
            columnExpressions.stream().sequential().map(stack -> stack.evaluate(null)).toList());
      } else {
        return Stream.empty();
      }
    } else {
      Stream<ScannableTreeNode> allFromStream;
      Stream<ScannableTreeNode> oneFromStream =
          ScannableTreeNode.forRoot(root).scan(this.rangedJsonPointers.get(0).toString());
      allFromStream = oneFromStream;
      if (rangedJsonPointers.size() == 2) {
        allFromStream =
            oneFromStream.flatMap(
                new Function<ScannableTreeNode, Stream<ScannableTreeNode>>() {
                  @Override
                  public Stream<ScannableTreeNode> apply(ScannableTreeNode t) {
                    var nextScan = ScannableTreeNode.forRoot(root);
                    nextScan.setContext(t.getContext());
                    return nextScan.scan(rangedJsonPointers.get(1).contextAware(t).toString());
                  }
                });
      }
      if (rangedJsonPointers.size() > 2) {
        throw new IllegalArgumentException(
            "Only two RANGED_JSON_POINTERs are supported in a query.");
      }

      Stream<ScannableTreeNode> filteredStream = allFromStream.filter(this::conditionOkay);
      Stream<ScannableTreeNode> orderedStream = filteredStream;
      if (ordering.isPresent()) {
        orderedStream =
            filteredStream.sorted(
                (stn1, stn2) -> {
                  TreePrimitive value1 = ordering.get().evaluate(stn1);
                  TreePrimitive value2 = ordering.get().evaluate(stn2);
                  int order = value1.compareTo(value2);
                  return orderDescending ? -order : order;
                });
      }
      return orderedStream
          .limit(limit.orElse(Integer.MAX_VALUE))
          .map(
              stn -> {
                return (columnExpressions.stream()
                    .sequential()
                    .map(stack -> stack.evaluate(stn))
                    .toList());
              });
    }
  }

  @Override
  public void enterSelectCore(TreeSQLParser.SelectCoreContext ctx) {
    ctx.children.forEach(
        child -> {
          if (child != null) {
            if (child instanceof TreeSQLParser.ResultColumnContext column) {
              if (column.columnAlias() != null) {
                String alias = column.columnAlias().getText();
                columnNames.add(alias);
              } else {
                columnNames.add(column.expr().getText());
              }
              columnExpressions.add(
                  new StackEvaluation(ExpressionParser.parseExpressionStack(column.expr())));
            } else if (child instanceof TreeSQLParser.JsonTextValueContext rJsonPointer) {
              TreeRangedJSONPointer rJSONPtr =
                  TreeValue.parseRangedJSONPointer(rJsonPointer.JSON_TEXT_VALUE().getText());
              rangedJsonPointers.add(rJSONPtr);
            }
          }
        });
  }

  @Override
  public void enterWhereExpr(WhereExprContext ctx) {
    whereCondition = new StackEvaluation(ExpressionParser.parseExpressionStack(ctx.expr()));
  }

  @Override
  public void enterOrderByStmt(OrderByStmtContext ctx) {
    var orderingTerm = ctx.orderingTerm();
    ordering =
        Optional.of(
            new StackEvaluation(ExpressionParser.parseExpressionStack(orderingTerm.expr())));
    orderDescending =
        (ctx.orderingTerm().ascOrDesc() != null && ctx.orderingTerm().ascOrDesc().DESC() != null)
            ? true
            : false;
  }

  @Override
  public void enterLimitStmt(LimitStmtContext ctx) {
    limit = Optional.of(Integer.parseInt(ctx.NUMERIC_LITERAL().getText()));
  }
}
