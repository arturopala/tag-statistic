package org.encalmo.tagstats;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Actor is a base abstract class of all asynchronous components, encapsulates message queue and multi-threaded execution details
 *
 * @param <T> type of the supported messages
 */
public abstract class Actor<T> {
    private final Executor executor;
    private final BlockingQueue<T> queue;
    private final AtomicBoolean open = new AtomicBoolean(true);

    private final Runnable consumer = new Runnable() {
        @Override
        public void run() {
            try {
                T message = queue.take();
                react(message);
            } catch (InterruptedException e) {
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
     * @param message message to be processed
     */
    protected abstract void react(T message);

    /**
     * Puts this message at the end of the queue
     *
     * @param message message to be processed in the future
     */
    protected final void enqueue(T message) {
        if (open.get()) {
            try {
                queue.put(message);
                executor.execute(consumer);
            } catch (InterruptedException e) {
            }
        } else {
            throw new ActorAlreadyShutdownException();
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
        open.set(false);
    }

}
