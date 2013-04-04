package org.encalmo.tagstatistic;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

public class GenericTagParser implements TagParser {
    @Override
    public void parse(Reader reader, TagStatistic<String> statistic) {
        CharBuffer buffer = CharBuffer.allocate(256);
        int i;
        try {
            while((i = reader.read())!=-1){
                processCharacter((char)i, buffer, statistic);
            }
            processCharacter(' ', buffer, statistic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void processCharacter(char ch, CharBuffer buffer, TagStatistic<String> statistic) {
        ch = Character.toLowerCase(ch);
        if(isTagDelimiter(ch)){
            registerTagOccurrence(buffer, statistic);
        } else if(isValidTagCharacter(ch)){
            rememberCharacter(ch, buffer);
        }
    }

    protected boolean isTagDelimiter(char ch) {
        return Character.isWhitespace(ch);
    }

    protected void registerTagOccurrence(CharBuffer buffer, TagStatistic<String> statistic) {
        String tag = pollTag(buffer);
        if(tag.length()>=5){
         statistic.increment(tag);
        }
    }

    protected boolean isValidTagCharacter(char ch) {
        return Character.isAlphabetic(ch) || Character.isDigit(ch);
    }

    protected void rememberCharacter(char ch, CharBuffer buffer) {
        if(buffer.remaining()>0){
            buffer.put(ch);
        } else {
            buffer.clear();
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
