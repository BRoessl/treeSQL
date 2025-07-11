package io.broessl.treesql.csv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import io.broessl.treesql.json.NavigableJsonNode;
import io.broessl.treesql.spi.NavigableTreeProvider;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.csv.CSVFormat;

public class NavigableCsvProvider implements NavigableTreeProvider {
  @Override
  public String getDirective() {
    return "CSV";
  }

  @Override
  public Optional<NavigableTreeNode> buildTreeRoot(TreeValue fromContent) {
    if (fromContent instanceof TreeString tString) {
      try {
        ArrayNode array = defaultParse(tString);
        return Optional.of(new NavigableJsonNode(array, null, null));
      } catch (Exception e) {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<NavigableTreeNode> attachTreeNode(
      String rootName, TreeValue fromContent, NavigableTreeNode parentNode, List<String> argument) {
    if (fromContent instanceof TreeString tString) {
      if (argument.isEmpty()) {
        try {
          ArrayNode array = defaultParse(tString);
          return Optional.of(new NavigableJsonNode(array, parentNode, rootName));
        } catch (Exception e) {
          return Optional.empty();
        }
      } else {
        String delimiter = null;
        String quote = null;
        List<String> hasNamedColumns = new ArrayList<>();
        for (String arg : argument) {
          if (arg.startsWith("delimiter=")) {
            delimiter = arg.substring("delimiter=".length());
          } else if (arg.startsWith("quote=")) {
            quote = arg.substring("quote=".length());
          } else if (arg.startsWith("has=")) {
            hasNamedColumns.add(arg.substring("has=".length()));
          }
        }
        try {
          CsvSettings settings =
              new CsvSettings(
                  delimiter, quote, hasNamedColumns); // NavigableJsonNode.OM.readValue(argAsJson,
          // CsvSettings.class);
          ArrayNode array = settingsParse(tString, settings);
          return Optional.of(new NavigableJsonNode(array, parentNode, rootName));

        } catch (JsonProcessingException e) {
          // log
        } catch (Exception e) {
          // ignore and skip
        }
      }
    } else {
      return Optional.empty();
    }
    return Optional.empty();
  }

  private ArrayNode settingsParse(TreeString tString, CsvSettings settings) throws IOException {
    var builder = CSVFormat.DEFAULT.builder();
    builder = settings.delimiter() != null ? builder.setDelimiter(settings.delimiter()) : builder;
    builder = settings.quote() != null ? builder.setQuote(settings.quote().charAt(0)) : builder;
    try (var parsedCsv = builder.setHeader().get().parse(new StringReader(tString.getValue()))) {
      var array = NavigableJsonNode.OM.createArrayNode();
      List<String> names = parsedCsv.getHeaderNames();
      for (String expectedHeader : settings.hasNamedColumns()) {
        if (!names.contains(expectedHeader)) {
          throw new IllegalArgumentException();
        }
      }
      parsedCsv.forEach(
          record -> {
            var jsonObject = NavigableJsonNode.OM.createObjectNode();
            for (int i = 0; i < names.size(); i++) {
              String name = names.get(i);
              String value = record.get(name);
              if (value == null) {
                jsonObject.set(name, NullNode.instance);
              } else {
                jsonObject.put(name, value);
              }
            }
            array.add(jsonObject);
          });
      return array;
    }
  }

  private ArrayNode defaultParse(TreeString tString) throws IOException {
    try (var parsedCsv =
        CSVFormat.DEFAULT
            .builder()
            .setAllowMissingColumnNames(false)
            .setHeader()
            .get()
            .parse(new StringReader(tString.getValue()))) {
      var array = NavigableJsonNode.OM.createArrayNode();
      List<String> names = parsedCsv.getHeaderNames();
      if (names.isEmpty() || names.size() < 2) {
        // simple workaround to avoid non-csv files misinterpreted as csv
        throw new IllegalArgumentException(
            "CSV must have at least one header with at least two columns.");
      }
      parsedCsv.forEach(
          record -> {
            var jsonObject = NavigableJsonNode.OM.createObjectNode();
            for (int i = 0; i < names.size(); i++) {
              String name = names.get(i);
              String value = record.get(name);
              if (value == null) {
                throw new IllegalArgumentException(
                    "CSV value for header '" + name + "' is null. Might be invalid CSV content.");
              } else {
                jsonObject.put(name, value);
              }
            }
            array.add(jsonObject);
          });
      return array;
    }
  }
}
