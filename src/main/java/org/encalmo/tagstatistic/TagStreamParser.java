package org.encalmo.tagstatistic;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Queue;

public class TagStreamParser implements TagParser {
    @Override
    public void parse(Reader reader, TagStatistic<String> statistic) {
        Queue<Character> queue = new ArrayDeque<>();
        int ch;
        try {
            while((ch = reader.read())!=-1){
                processCharacter(ch, queue, statistic);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void processCharacter(int ch, Queue<Character> queue, TagStatistic<String> statistic) {
        ch = Character.toLowerCase(ch);
        if(Character.isSpaceChar(ch)){
           String tag = pollTag(queue);
           if(tag.length()>=5){
            statistic.increment(tag);
           }
        } else if(Character.isAlphabetic(ch) || Character.isDigit(ch)){
           queue.add(Character.valueOf((char)ch));
        }
    }

    protected String pollTag(Queue<Character> queue) {
        char[] seq = new char[queue.size()];
        for(int i=0;i<seq.length;i++){
            seq[i] = queue.poll().charValue();
        }
        return String.valueOf(seq);
    }
}
