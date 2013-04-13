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
    private final TagStatsActor<String> tagStatsActor;

    public TagStatsFileEventListener(FileParserActor fileParserActor, TagStatsActor<String> tagStatsActor) {
        this.fileParserActor = fileParserActor;
        this.tagStatsActor = tagStatsActor;
    }

    @Override
    public void fileCreated(Path path) {
        fileParserActor.parse(path);
    }

    @Override
    public boolean initialFilesAlreadyProcessed() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
        while (!tagStatsActor.isQueueEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("\r\nTop tags (from all " + tagStatsActor.total() + "):\r\n-------------------");
        for (String tag : tagStatsActor.top()) {
            System.out.println(tag);
        }
        return true;
    }
}
