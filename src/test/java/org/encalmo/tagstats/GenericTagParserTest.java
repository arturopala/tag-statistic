package org.encalmo.tagstats;

import org.encalmo.actor.Callback;
import org.encalmo.util.AssertThat;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

public class GenericTagParserTest {

    @Test
    public void shouldParseStringAndReturnTop10Tags() {
        //given
        Reader r = new StringReader(TestData.TEST_STRING_1);
        TagSet<String> s = new GenericTagSet<>(10, 1);
        TagParserStrategy ps = new GenericTagParserStrategy(5);
        TagParser p = new GenericTagParser(s, ps);
        //when
        p.parse(r, Callback.EMPTY);
        Iterable<String> tags = s.top();
        //then
        AssertThat.sameElements(tags, TestData.EXPECTED_TOP10_TAGS_1);
    }

}
