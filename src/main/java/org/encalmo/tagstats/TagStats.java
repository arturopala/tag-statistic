package org.encalmo.tagstats;

/**
 * TagStat is an interface of components gathering tags and providing top stats.
 *
 * @param <T> type of the tags
 * @see TagStatsSet
 * @see TagStatsActor
 * @see GenericTagStatsService
 */
public interface TagStats<T> {

    void increment(T tag);

    Iterable<T> top();

    int total();
}
