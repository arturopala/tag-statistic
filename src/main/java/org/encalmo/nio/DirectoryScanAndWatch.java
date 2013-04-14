package org.encalmo.nio;

import org.encalmo.actor.Callback;
import org.encalmo.util.SingleThreadService;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.*;
import java.util.List;

/**
 * DirectoryScanAndWatch is a general purpose component
 * encapsulating directory scan and watching complexity behind simple {@link FileEventListener}
 * <p/>
 * This component is a {@link org.encalmo.util.SingleThreadService} running in a separate thread.
 */
public class DirectoryScanAndWatch extends SingleThreadService {

    private final Path path;
    private final WatchService watchService;
    private final FileEventListener listener;
    private Callback callback;
    private WatchKey watchKey;
    private File[] files;

    /**
     * New directory scan and watch task.
     *
     * @param path     path of the directory to watch
     * @param listener events listener
     * @param callback callback to be executed after initial files were processed
     * @throws Exception
     */
    public DirectoryScanAndWatch(Path path, FileEventListener listener, Callback callback) throws Exception {
        if ((path == null) || !Files.isDirectory(path)) {
            throw new AssertionError("provided path can not null and must be accessible directory");
        }
        if (listener == null) {
            throw new AssertionError("provided listener can not null");
        }
        if (callback == null) {
            throw new AssertionError("provided callback can not null");
        }
        this.path = path;
        this.listener = listener;
        this.callback = callback;
        this.watchService = path.getFileSystem().newWatchService();
    }

    @Override
    protected void init() throws Exception {
        this.files = path.toFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        this.watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Override
    protected void service() throws Exception {
        processInitialFiles();
        listen(watchService, watchKey);
    }

    @Override
    protected void destroy() {
        watchKey.cancel();
        watchKey = null;
    }

    private void processInitialFiles() {
        for (int i = 0; i < this.files.length - 1; i++) {
            listener.fileCreated(this.files[i].toPath(), Callback.EMPTY);
        }
        listener.fileCreated(this.files[this.files.length - 1].toPath(), callback);
        this.files = null;
        this.callback = null;
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
                                Path file = this.path.resolve((Path) event.context());
                                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                    listener.fileCreated(file, Callback.EMPTY);
                                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                    listener.fileModified(file);
                                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                    listener.fileDeleted(file);
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

    public Path getPath() {
        return path;
    }


}
