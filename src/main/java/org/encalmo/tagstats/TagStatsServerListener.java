package org.encalmo.tagstats;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;


public class TagStatsServerListener implements SocketServerListener {
    private final TagStats<String> tags;
    private final Charset charset;

    public TagStatsServerListener(TagStats<String> tags, Charset charset) {
        this.tags = tags;
        this.charset = charset;
    }

    @Override
    public void read(SelectionKey key) throws Exception {
        key.interestOps(SelectionKey.OP_WRITE);
    }

    @Override
    public void write(SelectionKey key) throws Exception {
        SocketChannel channel = (SocketChannel) key.channel();
        CharsetEncoder encoder = charset.newEncoder();
        Iterable<String> top = tags.top();
        StringBuffer stringBuffer = new StringBuffer();
        for (String tag : top) {
            stringBuffer.append(tag);
            stringBuffer.append("\n");
        }
        ByteBuffer buffer = encoder.encode(CharBuffer.wrap(stringBuffer));
        channel.write(buffer);
        key.cancel();
        channel.close();
    }
}
