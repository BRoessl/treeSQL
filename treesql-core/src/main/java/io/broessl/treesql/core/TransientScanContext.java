package io.broessl.treesql.core;

import io.broessl.treesql.core.types.TreeNodeIdentifier;
import io.broessl.treesql.core.types.TreeString;
import java.util.HashMap;
import java.util.Map;

public class TransientScanContext implements ScanContext {

  Map<String, Object> bindings = new HashMap<>();

  TransientScanContext parentContext = null;

  TreeNodeIdentifier stepName = new TreeString("");

  public TransientScanContext(
      TransientScanContext previousBindings, TreeNodeIdentifier stepOrNodeLiteral) {
    this.parentContext = previousBindings;
    this.stepName = stepOrNodeLiteral;
  }

  public TransientScanContext() {}

  public void bindPath(String rangeLiteral) {
    if (parentContext != null && parentContext.getBinding(rangeLiteral) != null) {
      Object alreadyBound = parentContext.getBinding(rangeLiteral);
      if (alreadyBound != null) {
        throw new IllegalStateException(
            String.format(
                "You can not add binding '%s' to '%s' since it is already bound to '%s'",
                rangeLiteral, getEvaluationPath(), alreadyBound));
      }
    }
    bindings.put("~" + rangeLiteral, getEvaluationPath());
  }

  public void addBinding(String bindingName, Object bindingValue) {
    if (parentContext != null && parentContext.getBinding(bindingName) != null) {
      Object alreadyBound = parentContext.getBinding(bindingName);
      if (alreadyBound != null) {
        throw new IllegalStateException(
            String.format(
                "You can not add binding '%s' to '%s' since it is already bound to '%s'",
                bindingValue, bindingValue, alreadyBound));
      }
    }
    bindings.put(bindingName, bindingValue);
  }

  @Override
  public ScanContext chain(TreeNodeIdentifier stepOrNodeLiteral) {
    return new TransientScanContext(this, stepOrNodeLiteral);
  }

  @Override
  public ScanContext chainWithPathBinding(
      TreeNodeIdentifier stepOrNodeLiteral, String rangeLiteral, NavigableTreeNode valueAt) {
    TransientScanContext chainedBinding = new TransientScanContext(this, stepOrNodeLiteral);
    chainedBinding.bindPath(rangeLiteral);
    chainedBinding.addBinding(rangeLiteral, stepOrNodeLiteral);
    chainedBinding.addBinding("@" + rangeLiteral, valueAt);
    return chainedBinding;
  }

  public Object getBinding(String bindingName) {
    Object value = bindings.get(bindingName);
    if (value != null) return value;
    if (parentContext != null) {
      return parentContext.getBinding(bindingName);
    }
    return null;
  }

  @Override
  public Map<String, Object> getPathBindings() {
    Map<String, Object> result;
    if (parentContext == null) {
      result = new HashMap<>();
    } else {
      result = parentContext.getPathBindings();
    }
    result.putAll(bindings);
    return result;
  }

  public String getEvaluationPath() {
    if (parentContext == null) {
      return stepName.getValue().toString();
    } else {
      return parentContext.getEvaluationPath() + "/" + stepName.getValue().toString();
    }
  }

  @Override
  public ScanContext asMutable() {
    return this;
  }

  @Override
  public ScanContext asImmutable() {
    return new SolidifiedJsonScanBindings(this.getPathBindings(), this.getEvaluationPath());
  }

  private static class SolidifiedJsonScanBindings implements ScanContext {

    Map<String, Object> bindings;

    String evalPath;

    private SolidifiedJsonScanBindings(Map<String, Object> bindings, String evalPath) {
      this.bindings = bindings;
      this.evalPath = evalPath;
    }

    @Override
    public String getEvaluationPath() {
      return evalPath;
    }

    @Override
    public Object getBinding(String bindingName) {
      return bindings.get(bindingName);
    }

    @Override
    public Map<String, Object> getPathBindings() {
      return bindings;
    }

    @Override
    public ScanContext asImmutable() {
      return this;
    }

    @Override
    public ScanContext chain(TreeNodeIdentifier node) {
      throw new UnsupportedOperationException("SolidifiedJsonScanBindings can not be chained");
    }

    @Override
    public ScanContext asMutable() {
      var result = new TransientScanContext(null, new TreeString(evalPath));
      bindings.forEach(result::addBinding);
      return result;
    }

    @Override
    public ScanContext chainWithPathBinding(
        TreeNodeIdentifier forNodeName, String bindingName, NavigableTreeNode valueAt) {
      throw new UnsupportedOperationException("SolidifiedJsonScanBindings can not be chained");
    }
  }
}
