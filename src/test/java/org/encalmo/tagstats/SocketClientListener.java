package org.encalmo.tagstats;

import java.nio.channels.SelectionKey;

public interface SocketClientListener {

    void connected(SelectionKey key) throws Exception;
}
