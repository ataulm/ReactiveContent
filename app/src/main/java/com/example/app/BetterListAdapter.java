package com.example.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class BetterListAdapter<E> extends BaseAdapter {

  private List<E> elements = Collections.emptyList();

  @Override public int getCount() {
    return elements.size();
  }

  @Override public E getItem(int position) {
    return elements.get(position);
  }

  @Override public final View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = newView(position, parent, LayoutInflater.from(parent.getContext()));
    }
    bindView(position, convertView);
    return convertView;
  }

  public void adapt(List<E> elements) {
    if (elements != null) {
      swap(elements);
    } else {
      swap(Collections.<E>emptyList());
    }
  }

  private void swap(List<E> elements) {
    if (this.elements instanceof Closeable) {
      try {
        ((Closeable) this.elements).close();
      } catch (IOException ignored) {
      }
    }
    this.elements = elements;
    notifyDataSetChanged();
  }

  public abstract View newView(int position, ViewGroup parent, LayoutInflater layoutInflater);

  public abstract void bindView(int position, View convertView);
}
