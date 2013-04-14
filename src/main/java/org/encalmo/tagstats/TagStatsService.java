package org.encalmo.tagstats;

import java.net.InetSocketAddress;
import java.nio.file.Path;

/**
 * Aggregated interface of TagSet related services.
 */
public interface TagStatsService extends TagSet<String>, TagParser, FileParser {

    InetSocketAddress getAddress();

    Path getPath();
}
