package org.encalmo.tagstats;

import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * FileParserActor encapsulates TagParserActor and acts as a proxy
 * responsible of queueing and opening files before they reach the TagParserActor.
 */
public class FileParserActor extends Actor<Path> implements FileParser {
  private final TagParser parser;

  public FileParserActor(TagParser parser) {
    this(parser, Executors.newSingleThreadExecutor(), 1024);
  }

  public FileParserActor(TagParser parser, Executor executor, int queueSize) {
    super(executor, queueSize);
    this.parser = parser;
  }

  @Override
  public void parse(Path path) {
    enqueue(path);
  }

  @Override
  protected void react(Path path) throws Exception {
    if (Files.isReadable(path)) {
      Reader reader = Files.newBufferedReader(path, Charset.defaultCharset());
      System.out.println("new file to parse: " + path);
      parser.parse(reader);
    } else {
      System.err.println("File " + path + " is not readable! Cannot parse tags.");
    }
  }
}
