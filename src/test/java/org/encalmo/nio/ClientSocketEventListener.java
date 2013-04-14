package org.encalmo.nio;

import java.nio.channels.SelectionKey;

public interface ClientSocketEventListener {

    void connected(SelectionKey key) throws Exception;
}
