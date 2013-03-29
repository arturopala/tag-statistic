package org.encalmo.tagstatistic;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class TagStatisticActor<T> implements TagStatistic<T> {
  private TagStatisticSet<T> tags = new TagStatisticSet<>();
  private BlockingQueue<T> queue = new LinkedBlockingQueue<>(16);

  private Runnable updater = new Runnable() {
    @Override
    public void run() {
      while (true) {
        try {
          T tag = queue.take();
          tags.increment(tag);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  };

  public TagStatisticActor() {
    this(Executors.newSingleThreadExecutor());
  }

  protected TagStatisticActor(Executor executor) {
    executor.execute(updater);
  }

  @Override
  public void increment(T tag) {
    try {
      queue.put(tag);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Iterable<T> top() {
    return tags.top();
  }

  @Override
  public double shareOf(T tag) {
    return tags.shareOf(tag);
  }
}
