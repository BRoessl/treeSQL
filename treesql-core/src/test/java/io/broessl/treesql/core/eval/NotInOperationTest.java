package io.broessl.treesql.core.eval;

import static org.junit.jupiter.api.Assertions.*;

import io.broessl.treesql.core.types.TreeBool;
import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeNumber;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import java.util.List;
import org.junit.jupiter.api.Test;

public class NotInOperationTest {
  private final NotInOperation op = new NotInOperation();

  @Test
  void testNotInListTrue() {
    TreeValue a = new TreeNumber(5);
    TreeValue b = new TreeList(List.of(new TreeNumber(1), new TreeNumber(10)));
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertTrue(((TreeBool) result).getValue());
  }

  @Test
  void testNotInListFalse() {
    TreeValue a = new TreeString("foo");
    TreeValue b = new TreeList(List.of(new TreeString("bar"), new TreeString("foo")));
    TreeValue result = op.call(new TreeValue[] {a, b});
    assertTrue(result instanceof TreeBool);
    assertFalse(((TreeBool) result).getValue());
  }

  @Test
  void testInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> op.call(new TreeValue[] {}));
    assertThrows(IllegalArgumentException.class, () -> op.call(null));
    assertThrows(
        IllegalArgumentException.class,
        () -> op.call(new TreeValue[] {new TreeNumber(1), new TreeNumber(2)}));
  }
}
