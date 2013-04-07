package org.encalmo.tagstats;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class DirectoryWatchService extends SingleThreadService {

    private final Path path;
    private final WatchService watchService;
    private final FileEventListener listener;
    private WatchKey watchKey;
    private File[] files;

    public DirectoryWatchService(Path path, FileEventListener listener) throws IOException {
        this.listener = listener;
        if (path == null || !Files.isDirectory(path))
            throw new AssertionError("provided path must be not null and accessible directory");
        this.path = path;
        this.watchService = path.getFileSystem().newWatchService();
    }

    @Override
    protected void init() throws IOException {
        this.files = path.toFile().listFiles();
        this.watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Override
    protected void service() throws Exception {
        boolean continueWatch = processInitialFiles();
        if (continueWatch) {
            listen(watchService, watchKey);
        } else {
            stop();
        }
    }

    @Override
    protected void destroy() {
        watchKey.cancel();
        watchKey = null;
    }

    private boolean processInitialFiles() throws IOException {
        for (File file : this.files) {
            if (file.isFile()) {
                listener.fileCreated(file.toPath());
            }
        }
        this.files = null;
        return listener.initialFilesAlreadyProcessed();
    }

    private void listen(WatchService watchService, WatchKey watchKey) {
        while (!Thread.interrupted()) {
            try {
                WatchKey key = watchService.take();
                if (key == watchKey) {
                    if (key != null) {
                        List<WatchEvent<?>> events = key.pollEvents();
                        for (WatchEvent event : events) {
                            try {
                                WatchEvent.Kind kind = event.kind();
                                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                    listener.fileCreated((Path) event.context());
                                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                    listener.fileModified((Path) event.context());
                                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                    listener.fileDeleted((Path) event.context());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        watchKey.reset();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
