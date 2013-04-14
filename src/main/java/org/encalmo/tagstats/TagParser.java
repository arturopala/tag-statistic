package org.encalmo.tagstats;

import org.encalmo.actor.Callback;

import java.io.Reader;


/**
 * TagParser is an interface of components parsing streams into tags.
 *
 * @see GenericTagParser
 * @see TagParserActor
 * @see GenericTagStatsService
 */
public interface TagParser {

    void parse(Reader reader, Callback callback);

}
