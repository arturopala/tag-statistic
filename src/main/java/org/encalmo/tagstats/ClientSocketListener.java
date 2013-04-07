package org.encalmo.tagstats;

import java.nio.channels.SelectionKey;

public interface ClientSocketListener {

    void connected(SelectionKey key) throws Exception;
}
