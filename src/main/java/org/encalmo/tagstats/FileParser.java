package org.encalmo.tagstats;

import org.encalmo.actor.Callback;

import java.nio.file.Path;

/**
 * FileParser is an interface of components parsing files.
 *
 * @see FileParserActor
 * @see GenericTagStatsService
 */
public interface FileParser {

    void parse(Path path, Callback callback);

}
