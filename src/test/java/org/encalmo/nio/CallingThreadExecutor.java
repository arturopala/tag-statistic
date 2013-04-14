package org.encalmo.nio;

import java.util.concurrent.Executor;

public class CallingThreadExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
