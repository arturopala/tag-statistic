package org.encalmo.tagstats;

/**
 * TagStat is an interface of components gathering tags and providing top stats.
 *
 * @param <T> type of tags
 */
public interface TagStats<T> {
  void increment(T tag);

  Iterable<T> top();

}
