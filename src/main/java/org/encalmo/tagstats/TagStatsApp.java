package org.encalmo.tagstats;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;


public class TagStatsApp implements TagStats<String> {
  private final NonBlockingServer server;
  private final DirectoryWatchService watch;
  private final TagStatsActor<String> tagStatsActor;

  public static void main(String[] args) throws Exception {
    if (args.length >= 4) {
      Properties p = new Properties();
      p.setProperty(args[0], args[1]);
      p.setProperty(args[2], args[3]);
      if (args.length >= 6) {
        p.setProperty(args[4], args[5]);
      }

      int port = Integer.parseInt(p.getProperty("-p"));
      String directory = p.getProperty("-d");
      int threads = Integer.parseInt(p.getProperty("-t", "0"));
      TagStatsApp app = new TagStatsApp(port, directory, threads);
      app.start();
      for (;;) {
        Thread.sleep(60000);
      }
    } else {
      throw new AssertionError("Usage: java -cp tagstats.jar " + TagStatsApp.class.getName() +
        " -p {port} -d {base directory} [-t {threads}]");
    }
  }

  public TagStatsApp(int port, String directory, int threads) throws Exception {
    if (directory == null) {
      throw new AssertionError("directory path must not be null");
    }
    if (threads <= 0) {
      threads = Runtime.getRuntime().availableProcessors() * 2;
    }

    final InetSocketAddress address = new InetSocketAddress(port);
    final Path path = FileSystems.getDefault().getPath(directory);
    final Charset charset = Charset.defaultCharset();
    final TagStatsSet<String> tags = new TagStatsSet<>();

    this.tagStatsActor = new TagStatsActor<>(tags);

    final TagParser parser = new GenericTagParser(tagStatsActor);
    final TagParserActor tagParserActor = new TagParserActor(parser, threads);
    final FileParserActor fileParserActor = new FileParserActor(tagParserActor);
    this.server = new NonBlockingServer(address, new TagStatsServerListener(tags, charset));
    this.watch = new DirectoryWatchService(path, new TagStatsDirectoryListener(fileParserActor, tags));
  }

  public synchronized void start() throws Exception {
    server.start();
    watch.start();
  }

  public synchronized void stop() throws Exception {
    watch.stop();
    server.stop();
  }

  @Override
  public void increment(String tag) {
    tagStatsActor.increment(tag);
  }

  @Override
  public Iterable<String> top() {
    return tagStatsActor.top();
  }
}
