package org.encalmo.tagstats;

import junit.framework.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.atomic.AtomicReference;

public class NonBlockingServerTest {

    @Test
    public void shouldOpenSocketAndListen() throws Exception {
        InetSocketAddress address = new InetSocketAddress(37243);
        final Charset charset = Charset.defaultCharset();
        final String message = "Hello, World!";
        final AtomicReference<String> result = new AtomicReference<>();
        NonBlockingServer server = new NonBlockingServer(address, new ServerSocketListener() {
            @Override
            public void read(SelectionKey key) throws Exception {
                SocketChannel channel = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                channel.read(buffer);
                channel.close();
                key.cancel();
                buffer.flip();
                CharsetDecoder decoder = charset.newDecoder();
                CharBuffer charBuffer = decoder.decode(buffer);
                result.set(charBuffer.toString());
            }

            @Override
            public void write(SelectionKey key) throws Exception {
                key.interestOps(SelectionKey.OP_READ);
            }
        });
        NonBlockingClient client = new NonBlockingClient(address, new ClientSocketListener() {
            @Override
            public void connected(SelectionKey key) throws Exception {
                SocketChannel channel = (SocketChannel) key.channel();
                CharsetEncoder encoder = charset.newEncoder();
                ByteBuffer buffer = encoder.encode(CharBuffer.wrap(message));
                channel.write(buffer);
                buffer.clear();
                channel.close();
                key.cancel();
            }
        });
        server.start();
        client.start();
        Thread.sleep(1000);
        Assert.assertEquals(message, result.get());
    }

}
