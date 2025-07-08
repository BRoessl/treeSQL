package io.broessl.treesql.sql;

import io.broessl.testutils.TestWithJsonData;
import io.broessl.treesql.core.types.TreeValue;
import io.broessl.treesql.json.NavigableJsonNode;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueryParserTest {

  @Test
  void testDoubledFromQueryExecution() throws Exception {
    String query =
        """
        SELECT foo, bar FROM "/~foo" JOIN "~foo/~bar" WHERE TRUE""";
    QueryParser parsedQuery = QueryParser.parseStatement(query);

    List<List<TreeValue>> result =
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

  @Test
  void testQueryLiteralUsageException() throws Exception {
    String query =
        """
        SELECT fo, bar FROM "/~foo" JOIN "~foo/~bar" WHERE TRUE""";
    var e =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> QueryParser.parseStatement(query));
    Assertions.assertEquals(
        "literal 'fo' is used in SELECT clause but only [foo, bar] are provided in FROM statement.",
        e.getMessage());
  }
}
