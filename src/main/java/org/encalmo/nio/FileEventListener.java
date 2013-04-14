package org.encalmo.nio;

import org.encalmo.actor.Callback;

import java.nio.file.Path;

/**
 * Listener of the events coming from {@link DirectoryScanAndWatch}.
 */
public interface FileEventListener {

    /**
     * Notification fired for every file creation
     */
    void fileCreated(Path path, Callback callback);

    /**
     * Notification fired for every file modification
     */
    void fileModified(Path path);

    /**
     * Notification fired for every file deletion
     */
    void fileDeleted(Path path);
}

