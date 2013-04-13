package org.encalmo.tagstats;

/**
 * TagParseStrategy is an interface of tags parsing strategies.
 *
 * @see GenericTagParseStrategy
 */
public interface TagParseStrategy {

    boolean isTagDelimiter(char ch);

    boolean isValidTagCharacter(char ch);

    boolean isValidTag(String tag);
}
