package org.encalmo.tagstats;

import java.nio.channels.SelectionKey;


/**
 * SocketServerListener is an interface complementing {@link MultiplexedSocketServer}
 */
public interface SocketServerListener {
    /**
     * Read from the socket channel represented by the SelectionKey
     *
     * @throws Exception
     */
    void read(SelectionKey key) throws Exception;

    /**
     * Write to the socket channel represented by the SelectionKey
     *
     * @throws Exception
     */
    void write(SelectionKey key) throws Exception;
}
