package io.broessl.treesql.csv;

import java.util.List;

public record CsvSettings(String delimiter, String quote, List<String> hasNamedColumns) {}
