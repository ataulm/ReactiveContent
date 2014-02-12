package rx.content;

import android.database.Cursor;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import rx.util.functions.Func1;

public class LazyList<E> implements List<E>, Closeable {
  private final Cursor cursor;
  private final Func1<Cursor, E> func1;

  public LazyList(Cursor cursor, Func1<Cursor, E> func1) {
    this.cursor = cursor;
    this.func1 = func1;
  }

  @Override public void close() throws IOException {
    cursor.close();
  }

  @Override public int size() {
    return cursor.isClosed() ? 0 : cursor.getCount();
  }

  @Override public boolean isEmpty() {
    return size() == 0;
  }

  @Override public E get(int location) {
    if (cursor.moveToPosition(location)) {
      return func1.call(cursor);
    }
    throw new IndexOutOfBoundsException();
  }

  @Override public Iterator<E> iterator() {
    return listIterator();
  }

  @Override public ListIterator<E> listIterator() {
    return listIterator(0);
  }

  @Override public ListIterator<E> listIterator(int location) {
    if (isEmpty()) {
      return new EmptyListIterator<E>();
    }
    return new LazyListIterator(location);
  }

  // Unsupported methods

  @Override public void add(int location, E object) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean add(E object) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean addAll(int location, Collection<? extends E> collection) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean addAll(Collection<? extends E> collection) {
    throw new UnsupportedOperationException();
  }

  @Override public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override public boolean contains(Object object) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean containsAll(Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override public int indexOf(Object object) {
    throw new UnsupportedOperationException();
  }

  @Override public int lastIndexOf(Object object) {
    throw new UnsupportedOperationException();
  }

  @Override public E remove(int location) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean remove(Object object) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean removeAll(Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean retainAll(Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override public E set(int location, E object) {
    throw new UnsupportedOperationException();
  }

  @Override public List<E> subList(int start, int end) {
    throw new UnsupportedOperationException();
  }

  @Override public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override public <T> T[] toArray(T[] array) {
    throw new UnsupportedOperationException();
  }

  private static class EmptyListIterator<E> implements ListIterator<E> {
    @Override public void add(E object) {
      throw new UnsupportedOperationException();
    }

    @Override public boolean hasNext() {
      return false;
    }

    @Override public boolean hasPrevious() {
      return false;
    }

    @Override public E next() {
      throw new UnsupportedOperationException();
    }

    @Override public int nextIndex() {
      throw new UnsupportedOperationException();
    }

    @Override public E previous() {
      throw new UnsupportedOperationException();
    }

    @Override public int previousIndex() {
      throw new UnsupportedOperationException();
    }

    @Override public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override public void set(E object) {
      throw new UnsupportedOperationException();
    }
  }

  private class LazyListIterator implements ListIterator<E> {
    private int index;

    public LazyListIterator(int location) {
      this.index = location;
    }

    @Override public void add(E object) {
      throw new UnsupportedOperationException();
    }

    @Override public boolean hasNext() {
      return index < cursor.getCount();
    }

    @Override public boolean hasPrevious() {
      return index > 0;
    }

    @Override public E next() {
      return get(index++);
    }

    @Override public int nextIndex() {
      return index + 1;
    }

    @Override public E previous() {
      return get(--index);
    }

    @Override public int previousIndex() {
      return index - 1;
    }

    @Override public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override public void set(E object) {
      throw new UnsupportedOperationException();
    }
  }
}
