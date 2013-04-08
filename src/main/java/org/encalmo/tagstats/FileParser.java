package org.encalmo.tagstats;

import java.nio.file.Path;


/**
 * FileParser is an interface of components parsing files.
 */
public interface FileParser {
  void parse(Path path);
}
