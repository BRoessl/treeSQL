package io.broessl.treesql.core.eval;

import java.util.HashMap;
import java.util.Map;

public class OperationRegistry {

  static final OperationRegistry INSTANCE = new OperationRegistry();

  public static OperationRegistry getInstance() {
    return INSTANCE;
  }

  private OperationRegistry() {
    registerOperation(new AddOperation());
    registerOperation(new SubtractOperation());
    registerOperation(new MultiplyOperation());
    registerOperation(new DivideOperation());
    registerOperation(new LessEqualOperation());
    registerOperation(new GreaterEqualOperation());
    registerOperation(new LessOperation());
    registerOperation(new GreaterOperation());
    registerOperation(new EqualsOperation());
    registerOperation(new NotEqualsOperation());
    registerOperation(new AndOperation());
    registerOperation(new OrOperation());
    registerOperation(new NotOperation());
    registerOperation(new InOperation());
    registerOperation(new NotInOperation());
    registerOperation(new ModuloOperation());
    registerOperation(new MatchOperation());
    registerOperation(new NotMatchOperation());
    registerOperation(new MaxOperation());
    registerOperation(new SinOperation());
  }

  private final Map<String, StackOperation> operations = new HashMap<>();

  private void registerOperation(StackOperation operation) {
    if (operation == null || operation.getSymbol() == null) {
      throw new IllegalArgumentException("Operation and its symbol must not be null");
    }
    if (operations.containsKey(operation.getSymbol())) {
      throw new IllegalArgumentException(
          "Operation with symbol " + operation.getSymbol() + " is already registered");
    }
    operations.put(operation.getSymbol(), operation);
  }

  public StackOperation getOperation(String symbol) {
    if (symbol == null || symbol.isEmpty()) {
      throw new IllegalArgumentException("Symbol must not be null or empty");
    }
    if (!operations.containsKey(symbol)) {
      throw new IllegalArgumentException("No operation registered for symbol: " + symbol);
    }
    return operations.get(symbol);
  }
}
