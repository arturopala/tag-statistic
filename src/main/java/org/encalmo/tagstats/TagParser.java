package org.encalmo.tagstats;

import java.io.Reader;


/**
 * TagParser is an interface of components parsing streams into tags.
 *
 * @see GenericTagParser
 * @see TagParserActor
 * @see TagStatsService
 */
public interface TagParser {

    void parse(Reader reader);

}
