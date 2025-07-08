package io.broessl.treesql.core.types;

import java.util.Objects;

public final class TreeString extends TreeNodeIdentifier {

  private String value;

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  public TreeString(String value) {
    Objects.requireNonNull(value);
    this.value = value;
  }

  @Override
  public int compareTo(TreePrimitive o) {
    return value.compareTo(((TreeString) o).value);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TreeString other = (TreeString) obj;
    if (value == null) {
      if (other.value != null) return false;
    } else if (!value.equals(other.value)) return false;
    return true;
  }
}
