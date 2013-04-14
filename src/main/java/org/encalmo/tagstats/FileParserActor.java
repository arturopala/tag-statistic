package org.encalmo.tagstats;

import org.encalmo.actor.Actor;
import org.encalmo.actor.Callback;

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
    public void parse(Path path, Callback callback) {
        enqueue(path, callback);
    }

    @Override
    protected void react(Path path, Callback callback) {
        if (Files.isReadable(path)) {
            try {
                Reader reader = Files.newBufferedReader(path, Charset.defaultCharset());
                System.out.println("[" + Thread.currentThread().getName() + "] new file to parse " + path);
                parser.parse(reader, callback);
            } catch (IOException e) {
                callback.failure(new IOException("Cannot open file " + path.toString()));
            }
        } else {
            callback.failure(new IOException("Path not readable " + path.toString()));
        }
    }
}
