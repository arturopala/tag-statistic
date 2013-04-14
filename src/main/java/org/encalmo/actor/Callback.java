package org.encalmo.actor;

/**
 * Callback interface used by {@link Actor} encapsulates the tasks
 * postponed until the message is successfully processed or execution failed.
 * <p/>
 * <ul>
 * <li>{@link #success()} should be called individually by the holder.
 * <li>{@link #failure(Throwable)} will be automatically called by the {@link Actor} after catching an exception,
 * but can be also called individually instead of throwing an exception.
 * </ul>
 */
public interface Callback {

    /**
     * Signals success.
     */
    void success();

    /**
     * Signals failure.
     *
     * @param cause cause of the failure
     */
    void failure(Throwable cause);

    /**
     * Empty {@link Callback},
     * if success does nothing,
     * if failure prints out stack trace.
     */
    Callback EMPTY = new Callback() {

        @Override
        public void success() {
        }

        @Override
        public void failure(Throwable cause) {
            cause.printStackTrace();
        }
    };
}
