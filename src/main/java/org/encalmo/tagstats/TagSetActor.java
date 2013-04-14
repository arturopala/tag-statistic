package org.encalmo.tagstats;

import org.encalmo.actor.Actor;
import org.encalmo.actor.Callback;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * TagSetActor encapsulates GenericTagSet and process all inserts using long queue
 * and single thread to avoid synchronization of concurrent inserts inside GenericTagSet.
 *
 * @param <T> type of tags
 */
public class TagSetActor<T> extends Actor<T> implements TagSet<T> {

    private TagSet<T> tags;

    public TagSetActor(TagSet<T> tags) {
        this(tags, Executors.newSingleThreadExecutor());
    }

    protected TagSetActor(TagSet<T> tags, Executor executor) {
        super(executor, 1024);
        this.tags = tags;
    }

    @Override
    protected void react(T tag, Callback callback) {
        tags.increment(tag);
        callback.success();
    }

    @Override
    public void increment(T tag) {
        try {
            this.enqueue(tag);
        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
        }
    }

    @Override
    public Iterable<T> top() {
        return tags.top();
    }

    @Override
    public int size() {
        return tags.size();
    }
}
