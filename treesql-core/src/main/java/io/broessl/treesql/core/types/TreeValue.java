package io.broessl.treesql.core.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;

public abstract sealed class TreeValue extends TreeStackableValue implements Comparable<TreeValue>
    permits TreeNull, TreeBool, TreeList, TreeNodeIdentifier {

  public abstract Object getValue();

  public static TreeValue createTreePrimitive(Object o) {

    if (o instanceof TreeValue prim) {
      return prim;
    }

    // consolidate JsonNode input
    if (o != null && o instanceof JsonNode jsonValue) {
      o = jsonValue.isNull() || jsonValue.isMissingNode() ? null : o;
      o = jsonValue.isTextual() ? jsonValue.textValue() : o;
      o = jsonValue.isBoolean() ? jsonValue.booleanValue() : o;
      o = jsonValue.isNumber() ? jsonValue.numberValue() : o;
      o = jsonValue instanceof ArrayNode arrNode ? arrNode.valueStream().toList() : o;
    }

    if (o == null) {
      return TreeNull.INSTANCE;
    }

    if (o instanceof Boolean b) {
      return new TreeBool(b);
    }

    if (o instanceof String s) {
      return new TreeString(s);
    }

    if (o instanceof Number n) {
      return new TreeNumber(n);
    }

    if (o instanceof List<?> list) {
      List<TreeValue> treePrimitives = new ArrayList<>();
      for (Object item : list) {
        treePrimitives.add(createTreePrimitive(item));
      }
      return new TreeList(treePrimitives);
    }

    throw new IllegalArgumentException("Unsupported type: " + o.getClass().getName());
  }

  public static <T extends TreeValue> T convert(Object o, Class<T> targetType) {

    // consolidate JsonNode input
    if (o != null && o instanceof JsonNode jsonValue) {
      o = jsonValue.isNull() || jsonValue.isMissingNode() ? null : o;
      o = jsonValue.isTextual() ? jsonValue.textValue() : o;
      o = jsonValue.isBoolean() ? jsonValue.booleanValue() : o;
      o = jsonValue.isNumber() ? jsonValue.numberValue() : o;
    }

    // consolidate TreePrimitive input
    o = o instanceof TreeValue prim ? prim.getValue() : o;

    if (targetType == TreeNull.class && (o == null || "null".equals(o) || "NULL".equals(o))) {
      return targetType.cast(TreeNull.INSTANCE);
    }

    if (o == null) {
      throw new IllegalArgumentException("null can only get converted to TreeNull");
    }

    if (targetType == TreeBool.class) {
      if (o instanceof Boolean b) {
        return targetType.cast(new TreeBool(b));
      }
      if (o instanceof String s) {
        return targetType.cast(new TreeBool(Boolean.parseBoolean(s)));
      }
      if (o instanceof Number n) {
        return targetType.cast(new TreeBool(n.doubleValue() != 0.0));
      }
      throw new IllegalArgumentException(
          "Cannot convert " + o.getClass().getName() + " to TreeBool");
    }

    if (targetType == TreeString.class) {
      return targetType.cast(new TreeString(o.toString()));
    }

    if (targetType == TreeNumber.class) {
      if (o instanceof Number n) {
        return targetType.cast(new TreeNumber(n));
      }
      if (o instanceof String s) {
        try {
          return targetType.cast(new TreeNumber(Double.parseDouble(s)));
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Cannot parse '" + s + "' as a number", e);
        }
      }
      if (o instanceof Boolean b) {
        return targetType.cast(new TreeNumber(b ? 1 : 0));
      }
      throw new IllegalArgumentException(
          "Cannot convert " + o.getClass().getName() + " to TreeNumber");
    }

    if (targetType == TreeList.class) {
      if (o instanceof Iterable<?> list) {
        List<TreeValue> treePrimitives = new ArrayList<>();
        for (Object item : list) {
          treePrimitives.add(createTreePrimitive(item));
        }
        return targetType.cast(new TreeList(treePrimitives));
      }
      // Convert single object to single-element list
      List<TreeValue> singleItemList = new ArrayList<>();
      singleItemList.add(createTreePrimitive(o));
      return targetType.cast(new TreeList(singleItemList));
    }

    throw new IllegalArgumentException("Unsupported target type: " + targetType.getName());
  }
}
