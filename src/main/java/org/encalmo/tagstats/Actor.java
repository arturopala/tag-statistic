package org.encalmo.tagstats;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Actor is a base abstract class of all asynchronous components, encapsulates message queue and multi-threaded execution details
 *
 * @param <T> type of supported messages
 */
public abstract class Actor<T> {
  private final Executor executor;
  private final BlockingQueue<T> queue;

  private final Runnable consumer = new Runnable() {
    @Override
    public void run() {
      try {
        T message = queue.take();
        react(message);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  };

  protected Actor(Executor executor, int queueSize) {
    this.executor = executor;
    this.queue = new LinkedBlockingQueue<>(queueSize);
  }

  /**
   * Reaction at the message should be implemented here
   *
   * @param message
   * @throws Exception
   */
  protected abstract void react(T message) throws Exception;

  /**
   * Puts this message at the end of the queue
   *
   * @param message
   */
  protected final void enqueue(T message) {
    try {
      queue.put(message);
      executor.execute(consumer);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean isQueueEmpty() {
    return queue.isEmpty();
  }

  /**
   * Initiates an orderly shutdown in which previously submitted
   * tasks are executed, but no new tasks will be accepted.
   */
  public void shutdown() {
    if (executor instanceof ExecutorService) {
      ((ExecutorService) executor).shutdown();
    }
  }

}
