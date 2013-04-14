package org.encalmo.actor;

/**
 * {@link Callback} factory and utilities.
 */
public class Callbacks {

    private Callbacks() {
    }

    /**
     * Empty {@link Runnable} task.
     */
    private static final Runnable EMPTY_TASK = new Runnable() {
        @Override
        public void run() {
        }
    };

    /**
     * Wraps {@link Runnable} ifSuccess task in {@link Callback}
     *
     * @param ifSuccess task to be done after successful processing of the message
     */
    public static Callback wrap(Runnable ifSuccess) {
        return wrap(ifSuccess, EMPTY_TASK);
    }

    /**
     * Wraps {@link Runnable} ifFailure task in {@link Callback}
     *
     * @param ifFailure task to be done after message processing failure
     */
    public static Callback wrapIfFailureTask(Runnable ifFailure) {
        return wrap(EMPTY_TASK, ifFailure);
    }

    /**
     * Wraps {@link Runnable} ifSuccess and ifFailure tasks in {@link Callback}
     *
     * @param ifSuccess task to be done after successful processing of the message
     */
    public static Callback wrap(Runnable ifSuccess, Runnable ifFailure) {
        return new RunnableWrapper(ifSuccess, ifFailure);
    }

    private static final class RunnableWrapper implements Callback {

        private final Runnable ifSuccess;
        private final Runnable ifFailure;

        public RunnableWrapper(Runnable ifSuccess, Runnable ifFailure) {
            if (ifSuccess == null) throw new NullPointerException("ifSuccess task can not be null");
            if (ifFailure == null) throw new NullPointerException("ifFailure task can not be null");
            this.ifSuccess = ifSuccess;
            this.ifFailure = ifFailure;
        }

        @Override
        public void success() {
            ifSuccess.run();
        }

        @Override
        public void failure(Throwable cause) {
            cause.printStackTrace();
            ifFailure.run();
        }
    }
}
