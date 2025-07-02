package io.broessl.treesql.core;

import io.broessl.treesql.core.types.TreeNodeIdentifier;
import java.util.Map;

public interface ScanContext {

  String getEvaluationPath();

  public Object getBinding(String bindingName);

  public Map<String, Object> getPathBindings();

  ScanContext asImmutable();

  ScanContext asMutable();

  ScanContext chain(TreeNodeIdentifier forNodeName);

  ScanContext chainWithPathBinding(
      TreeNodeIdentifier forNodeName, String bindingName, NavigableTreeNode valueAt);

  default String getAbsolutePath() {
    TreeScanExpression scanExpression = TreeScanExpression.parse(getEvaluationPath());
    scanExpression.toLiteralsOnly();
    return scanExpression.toString();
  }
}
