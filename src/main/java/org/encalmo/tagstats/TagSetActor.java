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
  private TagSet<T> tagSet;

  public TagSetActor(TagSet<T> tagSet) {
    this(tagSet, Executors.newSingleThreadExecutor());
  }

  protected TagSetActor(TagSet<T> tagSet, Executor executor) {
    super(executor, 1024);
    this.tagSet = tagSet;
  }

  @Override
  protected void react(T tag, Callback callback) {
    tagSet.increment(tag);
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
    return tagSet.top();
  }

  @Override
  public int size() {
    return tagSet.size();
  }

  @Override
  public boolean contains(T tag) {
    return tagSet.contains(tag);
  }
}
