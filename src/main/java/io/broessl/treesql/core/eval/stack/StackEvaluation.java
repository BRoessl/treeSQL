package io.broessl.treesql.core.eval.stack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.broessl.treesql.core.ScannableTreeNode;
import io.broessl.treesql.core.eval.StackOperation;
import io.broessl.treesql.core.types.TreeContextualPrimitive;
import io.broessl.treesql.core.types.TreePrimitive;

public class StackEvaluation {

    public StackEvaluation(List<Stackable> stackables) {
        if (stackables == null || stackables.isEmpty()) {
            throw new IllegalArgumentException("Stackables must not be null or empty");
        }
        this.stackables = Collections.unmodifiableList(stackables);
    }

    private final List<Stackable> stackables;

    public TreePrimitive evaluate(ScannableTreeNode ctx) {
        List<TreePrimitive> evalStack = new ArrayList<>();
        for (Stackable stackable : stackables) {
            if (stackable instanceof TreePrimitive primitive) {
                evalStack.add(primitive);
            } else if (stackable instanceof TreeContextualPrimitive evaluable) {
                TreePrimitive result = evaluable.getPrimitiveValue(ctx);
                Objects.requireNonNull(result, "Evaluable primitive must not return null: " + evaluable);
                evalStack.add(result);
            } else if (stackable instanceof StackOperation operation) {
                if (evalStack.size() < operation.getArgumentSize()) {
                    throw new IllegalArgumentException("Not enough arguments for operation: " + operation);
                }
                TreePrimitive[] args = new TreePrimitive[operation.getArgumentSize()];
                for (int i = args.length; i > 0; i--) {
                    args[i - 1] = evalStack.removeLast();
                }
                TreePrimitive operationResult = operation.call(args);
                evalStack.add(operationResult);

            } else {
                throw new IllegalArgumentException("Unknown stackable type: " + stackable.getClass().getName());
            }
        }
        if (evalStack.size() != 1) {
            throw new IllegalStateException(
                    "Evaluation stack should contain exactly one result, but has: " + evalStack.size());
        }
        return evalStack.get(0);
    }
}
