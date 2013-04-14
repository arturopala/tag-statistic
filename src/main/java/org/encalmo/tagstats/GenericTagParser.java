package org.encalmo.tagstats;

import org.encalmo.actor.Callback;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;


/**
 * GenericTagParser parses stream of characters into tags using {@link TagParserStrategy} and updates {@link TagSet} on the go.
 */
public class GenericTagParser implements TagParser {
    private final TagSet<String> tagSet;
    private final TagParserStrategy parserStrategy;

    public GenericTagParser(TagSet<String> tagSet, TagParserStrategy parserStrategy) {
        if (tagSet == null) {
            throw new AssertionError("tagSet must not be null");
        }
        if (parserStrategy == null) {
            throw new AssertionError("parserStrategy must not be null");
        }
        this.tagSet = tagSet;
        this.parserStrategy = parserStrategy;
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
                processCharacter((char) i, buffer, tagSet);
                counter++;
            }
            processCharacter(' ', buffer, tagSet);
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

    protected void processCharacter(char ch, CharBuffer buffer, TagSet<String> stats) {
        ch = Character.toLowerCase(ch);
        if (parserStrategy.isTagDelimiter(ch)) {
            registerTagOccurrence(buffer, stats);
        } else if (parserStrategy.isValidTagCharacter(ch)) {
            rememberCharacter(ch, buffer);
        }
    }

    protected void registerTagOccurrence(CharBuffer buffer, TagSet<String> stats) {
        if (buffer.remaining() > 0) {
            String tag = pollTag(buffer);
            String refinedTag = parserStrategy.refineTag(tag);
            if (refinedTag != null && parserStrategy.isValidTag(refinedTag)) {
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
