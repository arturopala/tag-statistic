package org.encalmo.tagstats;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * SingleThreadService is a base abstract class of manageable services running in the single dedicated thread,
 * encapsulates stop/start execution details.
 */
public abstract class SingleThreadService implements ManageableService {
    private ExecutorService executorService;

    /**
     * Component state initialization should be put here
     *
     * @throws Exception
     */
    protected abstract void init() throws Exception;

    /**
     * Main service job should be implemented here
     *
     * @throws Exception
     */
    protected abstract void service() throws Exception;

    /**
     * Cleanup code goes here
     *
     * @throws Exception
     */
    protected abstract void destroy() throws Exception;

    private final Runnable service = new Runnable() {
        @Override
        public void run() {
            try {
                service();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public synchronized void start() throws Exception {
        if (executorService == null) {
            init();
            this.executorService = Executors.newSingleThreadExecutor();
            executorService.execute(service);
        }

    }

    @Override
    public synchronized void stop() throws Exception {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
            destroy();
        }
    }
}
