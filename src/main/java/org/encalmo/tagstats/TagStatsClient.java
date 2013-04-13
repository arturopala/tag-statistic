package org.encalmo.tagstats;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class TagStatsClient {

    public static String[] readTop10TagsFromSocket(InetSocketAddress address, Charset charset) throws IOException {
        SocketChannel channel = SocketChannel.open();
        if (!channel.connect(address)) {
            channel.finishConnect();
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        channel.close();
        buffer.flip();
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = decoder.decode(buffer);
        String response = charBuffer.toString();
        System.out.println("[" + Thread.currentThread().getName() + "] response received " + response.length());
        return response.split("\n");
    }

}
