package org.encalmo.tagstats;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

public class TagStatsApp {

    private final NonBlockingServer server;
    private final DirectoryWatchService watch;

    public static void main(String[] args) throws Exception {
        if (args.length >= 4) {
            Properties p = new Properties();
            p.setProperty(args[0], args[1]);
            p.setProperty(args[2], args[3]);
            int port = Integer.parseInt(p.getProperty("-p"));
            String directory = p.getProperty("-d");
            TagStatsApp app = new TagStatsApp(port, directory);
            app.start();
        } else {
            throw new AssertionError("Usage: java -cp tagstats.jar " + TagStatsApp.class.getName() + " -p {port} -d {base directory}");
        }
    }

    public TagStatsApp(int port, String directory) throws Exception {
        if (directory == null) throw new AssertionError("directory path must not be null");
        int threads = Runtime.getRuntime().availableProcessors();
        final InetSocketAddress address = new InetSocketAddress(port);
        final Path path = FileSystems.getDefault().getPath(directory);
        final Charset charset = Charset.defaultCharset();
        final TagStats<String> tags = new TagStatsSet<>();
        final TagStatsActor<String> tagStatisticActor = new TagStatsActor<>(tags);
        final TagParser parser = new GenericTagParser(tagStatisticActor);
        final TagParserActor tagParserActor = new TagParserActor(parser, threads);
        final FileParserActor fileParserActor = new FileParserActor(tagParserActor, threads);
        server = new NonBlockingServer(address, new TagStatisticServerSocketListener(tags, charset));
        watch = new DirectoryWatchService(path, new SimpleFileEventListener() {
            @Override
            public void fileCreated(Path path) {
                fileParserActor.parse(path);
            }

            @Override
            public boolean initialFilesAlreadyProcessed() {
                for (String tag : tags.top()) {
                    System.out.println(tag);
                }
                return true;
            }
        });
    }

    public void start() throws Exception {
        server.start();
        watch.start();
    }

}
