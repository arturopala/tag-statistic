package org.encalmo.tagstats;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

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

    protected final void enqueue(T message) {
        try {
            queue.put(message);
            executor.execute(consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void react(T message) throws Exception;

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }
}
