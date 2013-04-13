package org.encalmo.tagstats;

/**
 * TagParseStrategy is an interface of object implementing parsing strategy.
 *
 * @see GenericTagParseStrategy, GenericTagParser
 */
public interface TagParseStrategy {

    /**
     * Returns true if the given character can be part of the tag,
     * if false character will be omitted
     */
    boolean isValidTagCharacter(char ch);

    /**
     * Returns true if the given character is tags delimiter.
     */
    boolean isTagDelimiter(char ch);

    /**
     * Returns refined version of the tag to be placed in the set,
     * returned tag will be still validated by {@link #isValidTag(String)}.
     */
    String refineTag(String tag);

    /**
     * Returns true if the given tag is valid, i.e. is long enough.
     * If false tag will be omitted.
     */
    boolean isValidTag(String tag);
}
