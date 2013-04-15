package org.encalmo.tagstats;

/**
 * TagSet is an interface of components gathering tags and providing the top list.
 *
 * @param <T> type of the tags
 * @see GenericTagSet
 * @see TagSetActor
 * @see GenericTagStatsService
 */
public interface TagSet<T> {
  void increment(T tag);

  Iterable<T> top();

  int size();

  boolean contains(T tag);
}
