package org.encalmo.tagstats;

import org.encalmo.actor.Callback;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;


/**
 * GenericTagParser parses stream of characters into tags using {@link TagParserStrategy} and updates {@link TagStats} on the go.
 */
public class GenericTagParser implements TagParser {
    private final TagStats<String> tagStats;
    private final TagParserStrategy parseStrategy;

    public GenericTagParser(TagStats<String> tagStats, TagParserStrategy parseStrategy) {
        if (tagStats == null) {
            throw new AssertionError("tagStats must not be null");
        }
        if (parseStrategy == null) {
            throw new AssertionError("parseStrategy must not be null");
        }
        this.tagStats = tagStats;
        this.parseStrategy = parseStrategy;
    }

    @Override
    public void parse(Reader reader, Callback callback) {
        if (reader == null) {
            throw new AssertionError("reader must not be null");
        }
        int counter = 0;
        long t0 = System.nanoTime();
        CharBuffer buffer = CharBuffer.allocate(256);
        int i;
        try {
            while ((i = reader.read()) != -1) {
                processCharacter((char) i, buffer, tagStats);
                counter++;
            }
            processCharacter(' ', buffer, tagStats);
            long t1 = System.nanoTime();
            System.out.println("[" + Thread.currentThread().getName() + "] " + counter + " characters long text parsed in " + (int) ((t1 - t0) / 1000000d) + "ms");
            callback.success();
        } catch (Exception e) {
            callback.failure(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

    protected void processCharacter(char ch, CharBuffer buffer, TagStats<String> stats) {
        ch = Character.toLowerCase(ch);
        if (parseStrategy.isTagDelimiter(ch)) {
            registerTagOccurrence(buffer, stats);
        } else if (parseStrategy.isValidTagCharacter(ch)) {
            rememberCharacter(ch, buffer);
        }
    }

    protected void registerTagOccurrence(CharBuffer buffer, TagStats<String> stats) {
        if (buffer.remaining() > 0) {
            String tag = pollTag(buffer);
            String refinedTag = parseStrategy.refineTag(tag);
            if (refinedTag != null && parseStrategy.isValidTag(refinedTag)) {
                stats.increment(refinedTag);
            }
        } else {
            buffer.clear();
        }
    }

    protected void rememberCharacter(char ch, CharBuffer buffer) {
        if (buffer.remaining() > 0) {
            buffer.put(ch);
        }
    }

    protected String pollTag(CharBuffer buffer) {
        char[] chars = new char[buffer.position()];
        buffer.rewind();
        buffer.get(chars);
        buffer.clear();
        return String.valueOf(chars);
    }
}
