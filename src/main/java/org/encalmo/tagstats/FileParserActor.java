package org.encalmo.tagstats;

import java.io.IOException;
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
        try {
            enqueue(path);
        } catch (Exception e) {
            throw new ParsingException(path.toString(), e);
        }
    }

    @Override
    protected void react(Path path) {
        if (Files.isReadable(path)) {
            Reader reader;
            try {
                reader = Files.newBufferedReader(path, Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException("Cannot open file " + path.toString());
            }
            System.out.println("[" + Thread.currentThread().getName() + "] new file to parse " + path);
            parser.parse(reader);
        } else {
            System.err.println("File " + path + " is not readable! Cannot parse tags.");
        }
    }
}
