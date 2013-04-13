package org.encalmo.tagstats;

import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * “Tag Stats” service reading and parsing in parallel multiple text files and finding the Top 10 frequently occurring words (tags).
 * The service maintain a real time in-memory version of the Top 10 Tag list.
 * The user can query the real time version of the list thorough a socket connection (if enabled).
 * The service scans given directory for existing and new text files  (if enabled).
 * <p/>
 * Default features:
 * <ul>
 * <li> The service processes multiple files at the same time.
 * <li> Tags are composed by a mix of letters and numbers and dash “-“. (Eg: 12313, abc123, 66-route, 66route,).
 * <li> Tags can also be numbers or a mix of letters and numbers. (Eg: 12313, abc123, 66route).
 * <li> The plural version of a word are counted as a separate tag. Eg: Bottle and Bottles are two different tags.
 * <li> Symbols, Space or punctuation marks are counted as tags.
 * <li> New lines, carriage returns, tabs and any white space are excluded.
 * <li> When the application completes processing it writes to standard output the top 10 tag list (neatly formatted)
 * <li> sorted in descending order based on number of occurrences.
 * <li> The application accepts multiple connections on port specified (as a command line parameter, -p).
 * <li> When the connection is made, the application prints the top 10 tag list (line by line) sorted in descending
 * <li> order based on the number of occurrences and closes the connection immediately.
 * </ul>
 */

public class TagStatsService implements TagStats<String>, TagParser, FileParser, ManageableService {

    private final MultiplexedSocketServer server;
    private final DirectoryScanAndWatch watch;
    private final TagStatsActor<String> tagStatsActor;
    private final FileParserActor fileParserActor;
    private final TagParserActor tagParserActor;
    private final InetSocketAddress address;


    public TagStatsService(TagStatsServiceConfig config) throws Exception {

        if (config.getTagParseStrategy() == null)
            throw new AssertionError("config parameter 'parserStrategy' should not be null");
        if (config.getThreads() < 1)
            throw new AssertionError("config parameter 'threads' should equal or greater than 1");

        final Charset charset = Charset.defaultCharset();
        final TagStatsSet<String> tags = new TagStatsSet<>();

        this.tagStatsActor = new TagStatsActor<>(tags);
        final TagParser parser = new GenericTagParser(tagStatsActor, config.getTagParseStrategy());
        this.tagParserActor = new TagParserActor(parser, config.getThreads());
        this.fileParserActor = new FileParserActor(tagParserActor);

        if (config.getPort() >= 0) {
            this.address = new InetSocketAddress(config.getPort());
            this.server = new MultiplexedSocketServer(address, new TagStatsServerListener(tags, charset));
        } else {
            this.address = null;
            this.server = null;
        }

        if (config.getDirectory() != null) {
            final Path path = FileSystems.getDefault().getPath(config.getDirectory());
            this.watch = new DirectoryScanAndWatch(path, new TagStatsDirectoryListener(fileParserActor, tags));
        } else {
            this.watch = null;
        }
    }

    public boolean isSocketServerEnabled() {
        return server != null;
    }

    public boolean isDirectoryWatchEnabled() {
        return watch != null;
    }

    public InetSocketAddress getAddress() {
        return address;
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
        tagStatsActor.increment(tag);
    }

    @Override
    public Iterable<String> top() {
        return tagStatsActor.top();
    }

    @Override
    public void parse(Path path) {
        fileParserActor.parse(path);
    }

    @Override
    public void parse(Reader reader) {
        tagParserActor.parse(reader);
    }

    public boolean isParsingQueueEmpty() {
        return tagParserActor.isQueueEmpty() && fileParserActor.isQueueEmpty();
    }
}
