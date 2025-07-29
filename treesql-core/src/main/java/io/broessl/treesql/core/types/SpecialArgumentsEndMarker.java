package io.broessl.treesql.core.types;

public final class SpecialArgumentsEndMarker extends TreeValue {

  public static final SpecialArgumentsEndMarker INSTANCE = new SpecialArgumentsEndMarker();

  private SpecialArgumentsEndMarker() {}

  @Override
  public int compareTo(TreeValue o) {
    throw new UnsupportedOperationException("Unimplemented method 'compareTo'");
  }

  @Override
  public Object getValue() {
    throw new UnsupportedOperationException("Unimplemented method 'getValue'.");
  }

  @Override
  public String toString() {
    return "#";
  }
}
