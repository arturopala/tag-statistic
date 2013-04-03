package org.encalmo.tagstatistic;

public interface TagStatistic<T> {
  void increment(T tag);

  Iterable<T> top();

}
