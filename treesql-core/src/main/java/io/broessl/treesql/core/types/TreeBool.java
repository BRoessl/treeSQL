package io.broessl.treesql.core.types;

public final class TreeBool extends TreeValue {

  private boolean value;

  @Override
  public Boolean getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value ? "TRUE" : "FALSE";
  }

  public TreeBool(boolean value) {
    this.value = value;
  }

  @Override
  public int compareTo(TreeValue o) {
    return Boolean.compare(value, ((TreeBool) o).value);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (value ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TreeBool other = (TreeBool) obj;
    if (value != other.value) return false;
    return true;
  }
}
