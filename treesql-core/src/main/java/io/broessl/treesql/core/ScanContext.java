package io.broessl.treesql.core;

import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.core.types.TreeValue;
import java.util.HashMap;
import java.util.Map;

public class ScanContext {

  final Map<String, Object> bindings = new HashMap<>();

  public ScanContext() {
    // Initialize with empty bindings
  }

  public ScanContext(Map<String, Object> initialBindings) {
    bindings.putAll(initialBindings);
  }

  public ScanContext(ScanContext initialContext) {
    bindings.putAll(initialContext.bindings);
  }

  public String getAbsolutePath(String bindingName) {
    String key = bindingName.startsWith("~") ? bindingName : "~" + bindingName;
    return (String) bindings.get(key);
  }

  public TreeValue getValueAt(String bindingName) {
    String key = bindingName.startsWith("@") ? bindingName : "@" + bindingName;
    return (TreeValue) bindings.get(key);
  }

  public TreeNodeIdentifier getNodeName(String bindingName) {
    return (TreeNodeIdentifier) bindings.get(bindingName);
  }

  public Object getObject(String bindingName) {
    return bindings.get(bindingName);
  }

  public ScanContext update(String bindingName, NavigableTreeNode bindedToNode) {
    bindings.put(bindingName, bindedToNode.getName());
    bindings.put("~" + bindingName, bindedToNode.absolutePath());
    bindings.put("@" + bindingName, bindedToNode.getValue());
    return this;
  }
}
