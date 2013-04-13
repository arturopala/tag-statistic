package org.encalmo.tagstats;

import java.nio.file.Path;

/**
 * Listener of the events coming from {@link DirectoryScanAndWatch}
 */
public interface FileEventListener {

    /**
     * Fired for every file creation
     */
    void fileCreated(Path path);

    /**
     * Fired for every file modification
     */
    void fileModified(Path path);

    /**
     * Fired for every file deletion
     */
    void fileDeleted(Path path);

    /**
     * Fired after all the initial files  were processed
     *
     * @return true if should watch for further directory changes or false otherwise
     */
    boolean initialFilesAlreadyProcessed();
}

/**
 * Simple {@link FileEventListener} abstract template
 */
abstract class SimpleFileEventListener implements FileEventListener {
    @Override
    public void fileCreated(Path path) {
    }

    @Override
    public void fileModified(Path path) {
    }

    @Override
    public void fileDeleted(Path path) {
    }

    @Override
    public boolean initialFilesAlreadyProcessed() {
        return true;
    }
}
