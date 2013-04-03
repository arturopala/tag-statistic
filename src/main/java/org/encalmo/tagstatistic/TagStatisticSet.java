package org.encalmo.tagstatistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


class TagStatisticSet<T> implements TagStatistic<T> {
  private class Element<T> {
    final T tag;
    int count = 1;
    Element<T> previous;
    Element<T> next;

    public Element(T tag) {
      this.tag = tag;
    }

  }

  private Map<T, Element<T>> tags = new HashMap<>();
  private Element<T> first;
  private Element<T> last;
  private AtomicReference<ArrayList<T>> top = new AtomicReference<>(new ArrayList<T>());
  private int total;

  @Override
  public void increment(T tag) {
    Element<T> elem = tags.get(tag);
    if (elem == null) {
      elem = new Element(tag);
      tags.put(tag, elem);
      append(last, elem);
    } else {
      elem.count++;
      adjust(elem);
    }
    total++;
    export();
  }

  @Override
  public Iterable<T> top() {
    return top.get();
  }

  public double shareOf(T tag) {
    Element<T> elem = tags.get(tag);
    if (elem != null) {
      return elem.count / (double) total;
    } else {
      return 0d;
    }
  }

  protected void append(Element<T> front, Element<T> back) {
    Element<T> moved = null;
    if (front != null) {
      moved = front.next;
      front.next = back;
    }
    back.previous = front;
    back.next = moved;
    if (moved != null) {
      moved.previous = back;
    }
    check(back);
  }

  protected void adjust(Element<T> elem) {
    if (elem.previous != null) {
      if (elem.previous.count < elem.count) {
        swap(elem);
        adjust(elem);
      }
    }
  }

  protected void swap(Element<T> elem) {
    Element<T> front = elem.previous;
    if (front != null) {
      // 2nd element front change
      elem.previous = front.previous;
      if (front.previous != null) {
        front.previous.next = elem;
      }

      // 2nd element back and 1st element front swap
      Element<T> back = elem.next;
      elem.next = front;
      front.previous = elem;

      // 1st element back change
      front.next = back;
      if (back != null) {
        back.previous = front;
      }
      check(front);
    }
    check(elem);
  }

  protected void check(Element<T> elem) {
    if (elem.next == null) {
      last = elem;
    }
    if (elem.previous == null) {
      first = elem;
    }
  }

  protected void export() {
    ArrayList<T> list = new ArrayList<>();
    Element<T> elem = first;
    while ((elem != null) && (list.size() < 10)) {
      list.add(elem.tag);
      elem = elem.next;
    }
    top.set(list);
  }

}
