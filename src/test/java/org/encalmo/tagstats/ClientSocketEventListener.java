package org.encalmo.tagstats;

import java.nio.channels.SelectionKey;

public interface ClientSocketEventListener {

    void connected(SelectionKey key) throws Exception;
}
