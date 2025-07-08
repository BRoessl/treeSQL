package io.broessl.treesql.cli;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.a7.A7_Grids;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import io.broessl.treesql.core.types.TreeValue;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ResultPrinter {

  private final Consumer<String> outputConsumer;
  private final int outputWidth;
  private long resultCounter = 0;

  public ResultPrinter(Consumer<String> outputConsumer, int outputWidth) {
    Objects.requireNonNull(outputConsumer, "Output consumer must not be null");
    this.outputConsumer = outputConsumer;
    this.outputWidth = outputWidth;
  }

  public void printHeader(List<String> headers) {
    if (resultCounter > 0) {
      throw new IllegalStateException(
          "You can not print a header after printing rows. Please create a new ResultPrinter instance.");
    }
    AsciiTable asciiTable = new AsciiTable();
    configureUniform(asciiTable);
    asciiTable.addStrongRule();
    var addedRow = asciiTable.addRow(headers);
    addedRow.setTextAlignment(TextAlignment.CENTER);
    asciiTable.addStrongRule();
    outputConsumer.accept(asciiTable.render());
    outputConsumer.accept(System.lineSeparator());
  }

  // this method configures the AsciiTable to have a uniform width and a specific
  // grid style which shoudl work on any terminal
  private void configureUniform(AsciiTable asciiTable) {
    asciiTable.getContext().setWidth(outputWidth);
    asciiTable.getContext().setGrid(A7_Grids.minusBarPlusEquals());
  }

  public void printRow(List<TreeValue> row) {
    List<String> rowValues = row.stream().map(Object::toString).toList();
    var valueTable = new AsciiTable();
    configureUniform(valueTable);
    var addedValueRow = valueTable.addRow(rowValues);
    addedValueRow.setPaddingLeft(1);
    valueTable.addRule();
    outputConsumer.accept(valueTable.render());
    outputConsumer.accept(System.lineSeparator());
    resultCounter++;
  }

  public void printFooter() {
    outputConsumer.accept(String.format("%d row(s) affected", resultCounter));
    outputConsumer.accept(System.lineSeparator());
    // completion time could be added here but unknown how to measure it currently
  }
}
