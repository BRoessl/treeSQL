package io.broessl.treesql.core.types;

public final class TreeNull extends TreeValue {

  public static final TreeNull INSTANCE = new TreeNull();

  private TreeNull() {}

  @Override
  public Object getValue() {
    return null;
  }

  @Override
  public String toString() {
    return "NULL";
  }

  @Override
  public int compareTo(TreeValue o) {
    if (equals(o)) {
      return 0;
    }
    throw new IllegalArgumentException(
        String.format(
            "'%s' is not comparable with '%s'",
            this.getClass().getSimpleName(), o.getClass().getSimpleName()));
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return true;
    if (getClass() != obj.getClass()) return false;
    return true;
  }
}
