package org.encalmo.tagstats;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TagStatsActor<T> extends Actor<T> implements TagStats<T> {

    private TagStats<T> tags;

    public TagStatsActor(TagStats<T> tags) {
        this(tags, Executors.newSingleThreadExecutor());
    }

    protected TagStatsActor(TagStats<T> tags, Executor executor) {
        super(executor, 1024);
        this.tags = tags;
    }

    @Override
    protected void react(T tag) {
        tags.increment(tag);
    }

    @Override
    public void increment(T tag) {
        this.enqueue(tag);
    }

    @Override
    public Iterable<T> top() {
        return tags.top();
    }
}
