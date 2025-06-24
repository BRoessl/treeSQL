package io.broessl.treesql.sql;

import io.broessl.testutils.TestWithJsonData;
import io.broessl.treesql.cli.ResultPrinter;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.json.NavigableJsonNode;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueryParserTest {

  @Test
  void testSimpleQueryExecutionNew() throws Exception {
    String query =
        """
                                SELECT bar, @bar AS value, ~bar FROM "/foo/~bar" WHERE 1 == 1""";
    QueryParser parsedQuery = QueryParser.parseStatement(query);
    var strb = new StringBuilder();
    ResultPrinter resultPrinter = new ResultPrinter(strb::append, 80);
    resultPrinter.printHeader(parsedQuery.getColumnNames());

    parsedQuery
        .execute(NavigableJsonNode.linkRoot(TestWithJsonData.testDataSimpleDataTree()))
        .sequential()
        .forEach(
            row -> {
              resultPrinter.printRow(row);
            });

    resultPrinter.printFooter();

    String printResult =
        """
                                ================================================================================
                                |           bar            |          value          |          ~bar           |
                                ================================================================================
                                | 0                        | bar                     | /foo/0                  |
                                +--------------------------+-------------------------+-------------------------+
                                | 1                        | baz                     | /foo/1                  |
                                +--------------------------+-------------------------+-------------------------+
                                2 row(s) affected
                                """;
    Assertions.assertEquals(printResult, strb.toString());
  }

  @Test
  void testDoubledFromQueryExecution() throws Exception {
    /*
     * var test = Stream.of("a","b", "c").flatMap(new
     * java.util.function.Function<String, Stream<String>>() {
     *
     * @Override
     * public Stream<String> apply(String t) {
     * return Stream.of(t + "1", t + "2");
     * }
     * }).toList();
     * Assertions.assertEquals("", test);
     * {
     * "X": {"A": {"X": "xax"}},
     * "Y": {"Y": {"A": "yya"}},
     * "C": {"C": {"C": "ccc"}}
     * }
     */

    String query =
        """
                                SELECT foo, bar FROM "/~foo" JOIN "~foo/~bar" WHERE TRUE""";
    QueryParser parsedQuery = QueryParser.parseStatement(query);

    List<List<TreePrimitive>> result =
        parsedQuery
            .execute(NavigableJsonNode.linkRoot(TestWithJsonData.testDataPattern()))
            .sequential()
            .toList();
    Assertions.assertEquals("X", result.get(0).get(0).toString());
    Assertions.assertEquals("A", result.get(0).get(1).toString());
    Assertions.assertEquals("Y", result.get(1).get(0).toString());
    Assertions.assertEquals("Y", result.get(1).get(1).toString());
    Assertions.assertEquals("C", result.get(2).get(0).toString());
    Assertions.assertEquals("C", result.get(2).get(1).toString());
  }
}
