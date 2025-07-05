package io.broessl.treesql.csv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreePrimitive;
import io.broessl.treesql.core.types.TreeString;
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
    return "~AS_CSV";
  }

  @Override
  public Optional<NavigableTreeNode> buildTreeRoot(TreePrimitive fromContent) {
    throw new UnsupportedOperationException(
        "NavigableCsvProvider does only support building attached tree nodes.");
  }

  @Override
  public Optional<NavigableTreeNode> attachTreeNode(
      TreePrimitive fromContent, NavigableTreeNode parentNode, List<String> argument) {
    if (fromContent instanceof TreeString tString) {
      if (argument.isEmpty()) {
        try {
          ArrayNode array = defaultParse(tString);
          return Optional.of(new NavigableJsonNode(array, parentNode, "!!CSV"));
        } catch (IOException e) {
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
          return Optional.of(new NavigableJsonNode(array, parentNode, "!!CSV"));

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
    try (var parsedCsv = builder.setHeader().get().parse(new StringReader(tString.nativeValue()))) {
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
            .setHeader()
            .get()
            .parse(new StringReader(tString.nativeValue()))) {
      var array = NavigableJsonNode.OM.createArrayNode();
      List<String> names = parsedCsv.getHeaderNames();
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
}
