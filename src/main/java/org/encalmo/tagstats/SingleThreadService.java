package org.encalmo.tagstats;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class SingleThreadService {
    private ExecutorService executorService;

    protected abstract void init() throws Exception;

    protected abstract void service() throws Exception;

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

    public synchronized void start() throws Exception {
        if (executorService == null) {
            init();
            this.executorService = Executors.newSingleThreadExecutor();
            executorService.execute(service);
        }

    }

    public synchronized void stop() throws Exception {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
            destroy();
        }
    }
}
