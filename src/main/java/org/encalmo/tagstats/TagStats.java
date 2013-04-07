package org.encalmo.tagstats;

public interface TagStats<T> {
    void increment(T tag);

    Iterable<T> top();

}
