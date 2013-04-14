package org.encalmo.actor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Actor is a base abstract class of components asynchronously processing messages,
 * encapsulates {@link BlockingQueue} and {@link Executor}.
 * <p/>
 * Together with the message {@link Callback} is transmitted.
 * After {@link #shutdown()} no messages are processed.
 *
 * @param <T> type of the supported messages
 */
public abstract class Actor<T> {

    private static class Envelope<T> {
        T message;
        Callback callback;
    }

    private final Executor executor;
    private final BlockingQueue<Object> queue;
    private final AtomicBoolean open = new AtomicBoolean(true);

    @SuppressWarnings("unchecked")
    private final Runnable consumer = new Runnable() {
        @Override
        public void run() {
            Envelope<T> envelope = null;
            try {
                Object object = queue.take();
                if (object instanceof Envelope) {
                    envelope = (Envelope) object;
                    react(envelope.message, envelope.callback);
                } else {
                    react((T) object, Callback.EMPTY);
                }
            } catch (Throwable e) {
                if (envelope != null) {
                    envelope.callback.failure(e);
                } else {
                    e.printStackTrace();
                }
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
    protected abstract void react(T message, Callback callback);

    /**
     * Puts this message at the end of the queue to be processed in the future
     *
     * @param message message to be processed
     */
    protected final void enqueue(T message) {
        enqueue(message, (Callback) null);
    }

    /**
     * Puts this message at the end of the queue to be processed in the future
     *
     * @param message   message to be processed
     * @param ifSuccess task to be done after successful processing of the message
     */
    protected final void enqueue(T message, Runnable ifSuccess) {
        enqueue(message, Callbacks.wrap(ifSuccess));
    }

    /**
     * Puts this message at the end of the queue to be processed in the future
     *
     * @param message  message to be processed
     * @param callback tasks to be done after successful processing of the message or after failure
     */
    protected final void enqueue(T message, Callback callback) {
        if (open.get()) {
            if (message == null) throw new NullPointerException("message to Actor can not be null");
            try {
                if (callback != null && callback != Callback.EMPTY) {
                    Envelope<T> envelope = new Envelope<>();
                    envelope.message = message;
                    envelope.callback = callback;
                    queue.put(envelope);
                } else {
                    queue.put(message);
                }
                executor.execute(consumer);
            } catch (InterruptedException e) {
            }
        } else {
            throw new ActorAlreadyShutdownException();
        }
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
