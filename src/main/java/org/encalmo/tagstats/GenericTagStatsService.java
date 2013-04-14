package org.encalmo.tagstats;

import org.encalmo.actor.Callback;
import org.encalmo.nio.DirectoryScanAndWatch;
import org.encalmo.nio.MultiplexedServerSocket;
import org.encalmo.util.ManageableService;

import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.file.Path;

/**
 * Generic {@link TagStatsService} implementation.
 *
 * @see TagStatsServiceFactory
 */
public class GenericTagStatsService implements TagStatsService, ManageableService {

    private final MultiplexedServerSocket server;
    private final DirectoryScanAndWatch watch;

    private final TagStats<String> tagStats;
    private final FileParser fileParser;
    private final TagParser tagParser;

    public GenericTagStatsService(TagStats<String> tagStats, TagParser tagParser, FileParser fileParser, MultiplexedServerSocket server, DirectoryScanAndWatch watch) {
        this.tagStats = tagStats;
        this.tagParser = tagParser;
        this.fileParser = fileParser;
        this.server = server;
        this.watch = watch;
    }

    public boolean isSocketServerEnabled() {
        return server != null;
    }

    public boolean isDirectoryWatchEnabled() {
        return watch != null;
    }

    @Override
    public InetSocketAddress getAddress() {
        return server != null ? server.getAddress() : null;
    }

    @Override
    public Path getPath() {
        return watch != null ? watch.getPath() : null;
    }

    @Override
    public synchronized void start() throws Exception {
        if (isSocketServerEnabled()) server.start();
        if (isDirectoryWatchEnabled()) watch.start();
    }

    @Override
    public synchronized void stop() throws Exception {
        if (isDirectoryWatchEnabled()) watch.stop();
        if (isSocketServerEnabled()) server.stop();
    }

    @Override
    public void increment(String tag) {
        tagStats.increment(tag);
    }

    @Override
    public Iterable<String> top() {
        return tagStats.top();
    }

    @Override
    public int total() {
        return tagStats.total();
    }

    @Override
    public void parse(Path path, Callback callback) {
        fileParser.parse(path, callback);
    }

    @Override
    public void parse(Reader reader, Callback callback) {
        tagParser.parse(reader, callback);
    }
}
