package org.encalmo.nio;

import junit.framework.Assert;
import org.encalmo.actor.Callback;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DirectoryScanAndWatchTest {

    @Test
    public void shouldWatchDirectory() throws Exception {
        //given
        final AtomicInteger files = new AtomicInteger(0);
        final AtomicInteger events = new AtomicInteger(0);
        final AtomicBoolean done = new AtomicBoolean(false);

        Path path = Paths.get("src/test/resources/watch");
        Callback callback = new Callback() {
            @Override
            public void success() {
                done.set(true);
            }

            @Override
            public void failure(Throwable cause) {
                Assert.fail(cause.getMessage());
            }
        };
        DirectoryScanAndWatch directoryScanAndWatch = new DirectoryScanAndWatch(path, new SimpleFileEventListener() {

            @Override
            public void fileCreated(Path path, Callback callback) {
                files.incrementAndGet();
                events.incrementAndGet();
                callback.success();
            }

            @Override
            public void fileDeleted(Path path) {
                files.decrementAndGet();
                events.incrementAndGet();
            }

        }, callback);
        //when
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
        //then
        assertEquals(2, files.get());
        assertEquals(22, events.get());
        assertTrue(done.get());
    }

}
