package org.encalmo.tagstats;

import java.nio.channels.SelectionKey;

public interface ServerSocketListener {

    void read(SelectionKey key) throws Exception;

    void write(SelectionKey key) throws Exception;
}
