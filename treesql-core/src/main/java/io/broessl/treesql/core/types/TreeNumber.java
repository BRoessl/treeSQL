package io.broessl.treesql.core.types;

import java.math.BigDecimal;
import java.util.Objects;

public final class TreeNumber extends TreeNodeIdentifier {

  private BigDecimal value;

  @Override
  public BigDecimal getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value.toPlainString();
  }

  public TreeNumber(Number value) {
    Objects.requireNonNull(value);
    if (value instanceof Double d) {
      this.value = BigDecimal.valueOf(d);
      return;
    }
    if (value instanceof Float f) {
      this.value = BigDecimal.valueOf(f);
      return;
    }
    if (value instanceof BigDecimal bd) {
      this.value = bd;
      return;
    }
    this.value = BigDecimal.valueOf(value.longValue());
  }

  public TreeNumber(Long value) {
    Objects.requireNonNull(value);
    this.value = BigDecimal.valueOf(value);
  }

  @Override
  public int compareTo(TreePrimitive o) {
    return value.compareTo(((TreeNumber) o).value);
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
    TreeNumber other = (TreeNumber) obj;
    if (value == null) {
      if (other.value != null) return false;
    } else if (!value.equals(other.value)) return false;
    return true;
  }
}
