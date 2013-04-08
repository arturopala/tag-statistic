package org.encalmo.tagstats;

import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;


/**
 * “Tag Stats” application reading and parsing multiple text files and finding the Top 10 frequently occurring words (tags).
 * The application maintain a real time in memory version of the Top 10 Tag list.
 * The user can query the real time version of the list thorough a socket connection.
 * <p/>
 * TagStatsApp acts as a main entry point and all components assembler.
 */
public class TagStatsApp implements TagStats<String>, TagParser, FileParser {
  private final NonBlockingServer server;
  private final DirectoryWatchService watch;
  private final TagStatsActor<String> tagStatsActor;
  private final FileParserActor fileParserActor;
  private final TagParserActor tagParserActor;

  public InetSocketAddress getAddress() {
    return address;
  }

  private final InetSocketAddress address;

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

    address = new InetSocketAddress(port);

    final Path path = FileSystems.getDefault().getPath(directory);
    final Charset charset = Charset.defaultCharset();
    final TagStatsSet<String> tags = new TagStatsSet<>();

    this.tagStatsActor = new TagStatsActor<>(tags);

    final TagParser parser = new GenericTagParser(tagStatsActor);
    this.tagParserActor = new TagParserActor(parser, threads);
    this.fileParserActor = new FileParserActor(tagParserActor);
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

  @Override
  public void parse(Path path) {
    fileParserActor.parse(path);
  }

  @Override
  public void parse(Reader reader) {
    tagParserActor.parse(reader);
  }
}
