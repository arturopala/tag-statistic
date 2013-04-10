package org.encalmo.tagstats;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SocketClient extends SingleThreadService {

    private final InetSocketAddress address;
    private final SocketClientListener listener;
    private Selector selector;
    private SocketChannel socketChannel;

    public SocketClient(InetSocketAddress address, SocketClientListener listener) {
        this.address = address;
        this.listener = listener;
    }

    @Override
    protected void init() throws Exception {
        connect(address);
    }

    @Override
    protected void service() throws Exception {
        talk(selector);
    }

    @Override
    protected void destroy() throws Exception {
        selector.close();
        socketChannel.close();
        selector = null;
        socketChannel = null;
    }

    private void connect(InetSocketAddress address) throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(address);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    private void talk(Selector selector) throws Exception {
        while (!Thread.interrupted() && selector.isOpen()) {
            int count = selector.select(500);
            if (count == 0) {
                continue;
            }
            Set keys = selector.selectedKeys();
            Iterator i = keys.iterator();
            while (i.hasNext()) {
                SelectionKey key = (SelectionKey) i.next();
                i.remove();
                SocketChannel channel = (SocketChannel) key.channel();
                if (key.isConnectable()) {
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }
                    listener.connected(key);
                    continue;
                }
            }
        }
    }
}
