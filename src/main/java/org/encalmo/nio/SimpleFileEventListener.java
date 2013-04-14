package org.encalmo.nio;

import org.encalmo.actor.Callback;

import java.nio.file.Path;

/**
 * Simple {@link FileEventListener} abstract template
 */
public abstract class SimpleFileEventListener implements FileEventListener {

    @Override
    public void fileCreated(Path path, Callback callback) {
    }

    @Override
    public void fileModified(Path path) {
    }

    @Override
    public void fileDeleted(Path path) {
    }
}
