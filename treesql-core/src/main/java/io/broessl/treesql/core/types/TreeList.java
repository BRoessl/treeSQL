package io.broessl.treesql.core.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TreeList extends TreeValue implements List<TreeValue> {

  private List<TreeValue> value = new ArrayList<>();

  @Override
  public List<Object> getValue() {
    return value.stream().map(p -> p.getValue()).toList();
  }

  @Override
  public String toString() {
    return "[" + value.stream().map(i -> i.toString()).collect(Collectors.joining(", ")) + "]";
  }

  public TreeList(List<TreeValue> value) {
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
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TreeList other = (TreeList) obj;
    if (value == null) {
      if (other.value != null) return false;
    } else if (!value.equals(other.value)) return false;
    return true;
  }

  @Override
  public int compareTo(TreeValue o) {
    return Integer.compare(value.size(), ((TreeList) o).value.size());
  }

  public void forEach(Consumer<? super TreeValue> action) {
    value.forEach(action);
  }

  public int size() {
    return value.size();
  }

  public boolean isEmpty() {
    return value.isEmpty();
  }

  public boolean contains(Object o) {
    return value.contains(o);
  }

  public Iterator<TreeValue> iterator() {
    return value.iterator();
  }

  public Object[] toArray() {
    return value.toArray();
  }

  public <T> T[] toArray(T[] a) {
    return value.toArray(a);
  }

  public boolean add(TreeValue e) {
    return value.add(e);
  }

  public boolean remove(Object o) {
    return value.remove(o);
  }

  public boolean containsAll(Collection<?> c) {
    return value.containsAll(c);
  }

  public boolean addAll(Collection<? extends TreeValue> c) {
    return value.addAll(c);
  }

  public boolean addAll(int index, Collection<? extends TreeValue> c) {
    return value.addAll(index, c);
  }

  public boolean removeAll(Collection<?> c) {
    return value.removeAll(c);
  }

  public boolean retainAll(Collection<?> c) {
    return value.retainAll(c);
  }

  public void replaceAll(UnaryOperator<TreeValue> operator) {
    value.replaceAll(operator);
  }

  public <T> T[] toArray(IntFunction<T[]> generator) {
    return value.toArray(generator);
  }

  public void sort(Comparator<? super TreeValue> c) {
    value.sort(c);
  }

  public void clear() {
    value.clear();
  }

  public TreeValue get(int index) {
    return value.get(index);
  }

  public TreeValue set(int index, TreeValue element) {
    return value.set(index, element);
  }

  public void add(int index, TreeValue element) {
    value.add(index, element);
  }

  public boolean removeIf(Predicate<? super TreeValue> filter) {
    return value.removeIf(filter);
  }

  public TreeValue remove(int index) {
    return value.remove(index);
  }

  public int indexOf(Object o) {
    return value.indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return value.lastIndexOf(o);
  }

  public ListIterator<TreeValue> listIterator() {
    return value.listIterator();
  }

  public ListIterator<TreeValue> listIterator(int index) {
    return value.listIterator(index);
  }

  public List<TreeValue> subList(int fromIndex, int toIndex) {
    return value.subList(fromIndex, toIndex);
  }

  public Spliterator<TreeValue> spliterator() {
    return value.spliterator();
  }

  public void addFirst(TreeValue e) {
    value.addFirst(e);
  }

  public void addLast(TreeValue e) {
    value.addLast(e);
  }

  public TreeValue getFirst() {
    return value.getFirst();
  }

  public Stream<TreeValue> stream() {
    return value.stream();
  }

  public TreeValue getLast() {
    return value.getLast();
  }

  public Stream<TreeValue> parallelStream() {
    return value.parallelStream();
  }

  public TreeValue removeFirst() {
    return value.removeFirst();
  }

  public TreeValue removeLast() {
    return value.removeLast();
  }

  public List<TreeValue> reversed() {
    return value.reversed();
  }
}
