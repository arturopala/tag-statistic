package org.encalmo.tagstats;

import org.encalmo.actor.Callback;
import org.encalmo.nio.SimpleFileEventListener;

import java.nio.file.Path;

/**
 * Parses newly added files.
 *
 * @see org.encalmo.nio.DirectoryScanAndWatch
 * @see GenericTagStatsService
 */
public class TagStatsFileEventListener extends SimpleFileEventListener {
    private final FileParser fileParser;

    public TagStatsFileEventListener(FileParser fileParser) {
        this.fileParser = fileParser;
    }

    @Override
    public void fileCreated(Path path, Callback callback) {
        fileParser.parse(path, callback);
    }
}
