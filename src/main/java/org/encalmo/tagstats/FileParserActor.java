package org.encalmo.tagstats;

import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


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
      parser.parse(reader);
    }
  }
}
