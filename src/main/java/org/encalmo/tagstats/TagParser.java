package org.encalmo.tagstats;

import java.io.Reader;


/**
 * TagParser is an interface of components parsing streams into tags.
 */
public interface TagParser {
  void parse(Reader reader);
}
