package org.encalmo.tagstats;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class DirectoryScanAndWatchTest {

    @Test
    public void shouldWatchDirectory() throws Exception {

        final AtomicInteger files = new AtomicInteger(0);
        final AtomicInteger events = new AtomicInteger(0);

        Path path = Paths.get("src/test/resources/watch");
        DirectoryScanAndWatch directoryScanAndWatch = new DirectoryScanAndWatch(path, new SimpleFileEventListener() {

            @Override
            public void fileCreated(Path path) {
                files.incrementAndGet();
                events.incrementAndGet();
            }

            @Override
            public void fileDeleted(Path path) {
                files.decrementAndGet();
                events.incrementAndGet();
            }

        });
        Files.createFile(path.resolve("file0.txt"));
        Files.delete(path.resolve("file0.txt"));
        directoryScanAndWatch.start();
        for (int i = 1; i < 11; i++) {
            Files.createFile(path.resolve("file" + i + ".txt"));
        }
        Thread.sleep(500);
        for (int i = 1; i < 11; i++) {
            Files.delete(path.resolve("file" + i + ".txt"));
        }
        Thread.sleep(500);
        directoryScanAndWatch.stop();
        Files.createFile(path.resolve("file11.txt"));
        Files.delete(path.resolve("file11.txt"));
        assertEquals(2, files.get());
        assertEquals(22, events.get());
    }

}
