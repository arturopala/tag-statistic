package org.encalmo.tagstats;

import org.encalmo.nio.ServerSocketEventListener;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Writes out TopN tags to the socket channel and closes it.
 *
 * @see org.encalmo.nio.MultiplexedServerSocket
 * @see GenericTagStatsService
 */
public class TagStatsServerSocketEventListener implements ServerSocketEventListener {
    private final TagSet<String> tagSet;
    private final Charset charset;

    public TagStatsServerSocketEventListener(TagSet<String> tagSet, Charset charset) {
        this.tagSet = tagSet;
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
        Iterable<String> top = tagSet.top();
        StringBuffer stringBuffer = new StringBuffer();
        for (String tag : top) {
            stringBuffer.append(tag);
            stringBuffer.append("\n");
        }
        ByteBuffer buffer = encoder.encode(CharBuffer.wrap(stringBuffer));
        if (channel.isOpen()) {
            channel.write(buffer);
        }
        key.cancel();
        channel.close();
    }
}
