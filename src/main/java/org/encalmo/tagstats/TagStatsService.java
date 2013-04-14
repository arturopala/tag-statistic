package org.encalmo.tagstats;

import java.net.InetSocketAddress;
import java.nio.file.Path;

/**
 * Aggregated interface of TagStats related services.
 */
public interface TagStatsService extends TagStats<String>, TagParser, FileParser {

    InetSocketAddress getAddress();

    Path getPath();
}
