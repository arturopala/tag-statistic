package org.encalmo.tagstats;

import org.encalmo.actor.Callbacks;
import org.encalmo.nio.DirectoryScanAndWatch;
import org.encalmo.nio.MultiplexedServerSocket;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Factory of {@link TagStatsService} instances.
 */
public class TagStatsServiceFactory {

    private TagStatsServiceFactory() {
    }

    /**
     * Creates asynchronous, multi-threaded {@link TagStatsService} based on actors.
     *
     * @param config configuration parameters
     * @throws Exception
     * @see GenericTagStatsService
     * @see TagStatsActor
     * @see TagParserActor
     * @see FileParserActor
     */
    public static GenericTagStatsService createAsynchronous(TagStatsServiceConfig config) throws Exception {
        if (config.getTagParserStrategy() == null)
            throw new AssertionError("config parameter 'parserStrategy' should not be null");
        if (config.getNumberOfThreads() < 1 || config.getNumberOfThreads() > 50)
            throw new AssertionError("config parameter 'threads' should be equal or greater than 1 and less than 50");
        if (config.getTopListSize() < 1 || config.getTopListSize() > 100)
            throw new AssertionError("config parameter 'topListSize' should be equal or greater than 1 and less than 100");
        if (config.getTopListUpdateAccuracy() < 1 || config.getTopListUpdateAccuracy() > 100)
            throw new AssertionError("config parameter 'accuracy' should be equal or greater than 1 and less than 100");

        final Charset charset = Charset.defaultCharset();
        final TagStatsSet<String> tags = new TagStatsSet<>(config.getTopListSize(), config.getTopListUpdateAccuracy());

        final TagStats<String> tagStats = new TagStatsActor<>(tags);
        final TagParser parser = new GenericTagParser(tagStats, config.getTagParserStrategy());
        TagParser tagParser = new TagParserActor(parser, config.getNumberOfThreads());
        FileParser fileParser = new FileParserActor(tagParser);
        MultiplexedServerSocket server;
        if (config.getServerSocketPort() >= 0) {
            InetSocketAddress address = new InetSocketAddress(config.getServerSocketPort());
            server = new MultiplexedServerSocket(address, new TagStatsServerSocketEventListener(tags, charset));
        } else {
            server = null;
        }
        DirectoryScanAndWatch watch;
        if (config.getBaseDirectory() != null) {
            final Path path = FileSystems.getDefault().getPath(config.getBaseDirectory());
            watch = new DirectoryScanAndWatch(path, new TagStatsFileEventListener(fileParser), Callbacks.wrap(new Runnable() {
                @Override
                public void run() {
                    System.out.println("\r\nTop tags (from all " + tagStats.total() + "):\r\n-------------------");
                    for (String tag : tagStats.top()) {
                        System.out.println(tag);
                    }
                }
            }));
        } else {
            watch = null;
        }
        return new GenericTagStatsService(tagStats, tagParser, fileParser, server, watch);
    }
}
