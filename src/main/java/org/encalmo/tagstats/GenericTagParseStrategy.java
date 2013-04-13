package org.encalmo.tagstats;

/**
 * Generic tags parsing strategy:
 * <ul>
 * <li> Tags are composed by a mix of letters and numbers and dash “-“. (Eg: 12313, abc123, 66-route, 66route,).
 * <li> Tags can also be numbers or a mix of letters and numbers. (Eg: 12313, abc123, 66route).
 * <li> The plural version of a word are counted as a separate tag. Eg: Bottle and Bottles are two different tags.
 * <li> Symbols, Space or punctuation marks are counted as tags.
 * <li> New lines, carriage returns, tabs and any white space are excluded.
 * </ul>
 */
public class GenericTagParseStrategy implements TagParseStrategy {

    private final int minTagLength;

    public GenericTagParseStrategy(int minTagLength) {
        this.minTagLength = minTagLength;
    }

    @Override
    public boolean isTagDelimiter(char ch) {
        return Character.isWhitespace(ch) || (ch == ',');
    }

    @Override
    public boolean isValidTag(String tag) {
        return tag.length() >= minTagLength;
    }

    @Override
    public boolean isValidTagCharacter(char ch) {
        return Character.isAlphabetic(ch) || Character.isDigit(ch);
    }
}