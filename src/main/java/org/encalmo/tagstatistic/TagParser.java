package org.encalmo.tagstatistic;

import java.io.Reader;

public interface TagParser {

    void parse(Reader reader, TagStatistic<String> statistic);
}
