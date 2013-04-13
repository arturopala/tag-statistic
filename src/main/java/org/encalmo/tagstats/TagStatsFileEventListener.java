package org.encalmo.tagstats;

import java.nio.file.Path;

/**
 * Prints out TopN tags after all initial text files were processed.
 *
 * @see DirectoryScanAndWatch
 * @see TagStatsService
 */
public class TagStatsFileEventListener extends SimpleFileEventListener {
    private final FileParserActor fileParserActor;
    private final TagStats<String> tags;

    public TagStatsFileEventListener(FileParserActor fileParserActor, TagStats<String> tags) {
        this.fileParserActor = fileParserActor;
        this.tags = tags;
    }

    @Override
    public void fileCreated(Path path) {
        fileParserActor.parse(path);
    }

    @Override
    public boolean initialFilesAlreadyProcessed() {
        while (!fileParserActor.isQueueEmpty()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("\r\nTop 10 tags:\r\n-------------------");
        for (String tag : tags.top()) {
            System.out.println(tag);
        }
        return true;
    }
}
