package org.encalmo.tagstats;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

public class GenericTagParser implements TagParser {

    private final TagStats<String> tagStats;

    public GenericTagParser(TagStats<String> tagStats) {
        if (tagStats == null) throw new AssertionError("tagStats must not be null");
        this.tagStats = tagStats;
    }

    @Override
    public void parse(Reader reader) {
        if (reader == null) throw new AssertionError("reader must not be null");
        CharBuffer buffer = CharBuffer.allocate(256);
        int i;
        try {
            while ((i = reader.read()) != -1) {
                processCharacter((char) i, buffer, tagStats);
            }
            processCharacter(' ', buffer, tagStats);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    protected void processCharacter(char ch, CharBuffer buffer, TagStats<String> statistic) {
        ch = Character.toLowerCase(ch);
        if (isTagDelimiter(ch)) {
            registerTagOccurrence(buffer, statistic);
        } else if (isValidTagCharacter(ch)) {
            rememberCharacter(ch, buffer);
        }
    }

    protected boolean isTagDelimiter(char ch) {
        return Character.isWhitespace(ch) || ch == ',';
    }

    protected void registerTagOccurrence(CharBuffer buffer, TagStats<String> statistic) {
        if (buffer.remaining() > 0) {
            String tag = pollTag(buffer);
            if (tag.length() >= 5) {
                statistic.increment(tag);
            }
        } else {
            buffer.clear();
        }
    }

    protected boolean isValidTagCharacter(char ch) {
        return Character.isAlphabetic(ch) || Character.isDigit(ch);
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
