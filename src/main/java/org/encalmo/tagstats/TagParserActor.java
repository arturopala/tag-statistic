package org.encalmo.tagstats;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TagParserActor extends Actor<Reader> implements TagParser {

    private TagParser parser;

    public TagParserActor(TagParser parser, int threads) {
        this(parser, Executors.newFixedThreadPool(threads), threads);
    }

    protected TagParserActor(TagParser parser, Executor executor, int queueSize) {
        super(executor, queueSize);
        this.parser = parser;
    }

    @Override
    protected void react(Reader reader) throws IOException {
        parser.parse(reader);
    }

    @Override
    public void parse(Reader reader) {
        enqueue(reader);
    }
}
