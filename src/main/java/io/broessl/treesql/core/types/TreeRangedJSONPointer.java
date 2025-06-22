package io.broessl.treesql.core.types;

import java.util.List;

import io.broessl.treesql.core.ScannableTreeNode;

public final class TreeRangedJSONPointer extends TreeContextualPrimitive {

    private final String selection;

    public TreeRangedJSONPointer(String pointer) {
        this.selection = pointer;
    }

    @Override
    public String toString() {
        return selection;
    }

    @Override
    public TreePrimitive getPrimitiveValue(ScannableTreeNode stn) {
        if (this.isContextAware()) {
            return this.contextAware(stn).getPrimitiveValue(stn);
        }
        stn.getNavigableTreeNode().getRoot();
        List<ScannableTreeNode> resultOfPointer = ScannableTreeNode
                .forRoot(stn.getNavigableTreeNode().getRoot()).scan(this.selection).toList();
        if (resultOfPointer.isEmpty()) {
            return TreeNull.INSTANCE;
        }
        if (resultOfPointer.size() == 1) {
            return new TreeString(resultOfPointer.get(0).getNavigableTreeNode().getValue().toString());
        }
        if (resultOfPointer.size() > 1) {
            return new TreeList(resultOfPointer.stream()
                    .map(n -> new TreeString(n.getNavigableTreeNode().getValue().toString())).toList());
        }
        throw new IllegalStateException();
    }

    public TreeRangedJSONPointer contextAware(ScannableTreeNode stn) {
        String pointer;
        if (isContextAware()) {
            String context;
            if (this.selection.contains("/")) {
                context = selection.substring(0, selection.indexOf("/"));
                String pathToContext = expectAsString(stn, context).nativeValue();
                pointer = pathToContext + selection.substring(selection.indexOf("/"));
                return new TreeRangedJSONPointer(pointer);
            } else {
                context = selection;
                String pathToContext = expectAsString(stn, context).nativeValue();
                pointer = pathToContext;
                return new TreeRangedJSONPointer(pointer);
            }
        } else {
            return this;
        }
    }

    private boolean isContextAware() {
        return this.selection.startsWith("~");
    }

}
