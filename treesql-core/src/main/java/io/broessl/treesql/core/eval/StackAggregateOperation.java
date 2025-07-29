package io.broessl.treesql.core.eval;

import io.broessl.treesql.core.types.TreeList;
import io.broessl.treesql.core.types.TreeValue;
import java.util.ArrayList;
import java.util.List;

// SUM AVG MIN MAX COUNT
public abstract class StackAggregateOperation extends StackOperation {

  static FlatOperation flatter = new FlatOperation();

  public int getArgumentSize() {
    return -1;
  }

  static List<TreeValue> flatArguments(TreeValue[] arguments) {
    List<TreeValue> flattenList = new ArrayList<>();
    for (TreeValue treeValue : arguments) {
      if (treeValue instanceof TreeList innerList) {
        flattenList.addAll(flatListDepthFirst(innerList));
      } else {
        flattenList.add(treeValue);
      }
    }
    return flattenList;
  }

  static List<TreeValue> flatListDepthFirst(TreeList listToFlatten) {
    boolean containsList = false;
    for (TreeValue treeValue : listToFlatten) {
      if (treeValue instanceof TreeList) {
        containsList = true;
        break;
      }
    }
    if (!containsList) {
      return listToFlatten;
    }
    List<TreeValue> flattenList = new ArrayList<>();
    for (TreeValue treeValue : listToFlatten) {
      if (treeValue instanceof TreeList deeperList) {
        flattenList.addAll(flatListDepthFirst(deeperList));
      } else {
        flattenList.add(treeValue);
      }
    }
    return flattenList;
  }
}
