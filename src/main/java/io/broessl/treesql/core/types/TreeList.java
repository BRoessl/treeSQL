package io.broessl.treesql.core.types;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class TreeList extends TreePrimitive {

    private List<? extends TreePrimitive> value = new ArrayList<>();

    @Override
    public List<? extends TreePrimitive> nativeValue() {
        return value;
    }

    @Override
    public String toString() {
        return "[" + value.stream().map(i -> i.toString()).collect(Collectors.joining(", ")) + "]";
    }

    public TreeList(List<? extends TreePrimitive> value) {
        if (value != null) {
            this.value = value;
        } else {
            value = new ArrayList<>();
        }
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TreeList other = (TreeList) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }


    @Override
    public int compareTo(TreePrimitive o) {
        return Integer.compare(value.size(), ((TreeList)o).value.size());
    }

}