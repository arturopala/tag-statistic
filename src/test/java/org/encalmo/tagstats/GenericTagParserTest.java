package org.encalmo.tagstats;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

public class GenericTagParserTest {

    @Test
    public void shouldParseStringAndReturnTop10Tags() {
        //given
        Reader r = new StringReader(TestData.TEST_STRING_1);
        TagStats<String> s = new TagStatsSet<>();
        TagParser p = new GenericTagParser(s);
        //when
        p.parse(r);
        Iterable<String> tags = s.top();
        //then
        AssertThat.sameElements(tags, TestData.EXPECTED_TOP10_TAGS_1);
    }

}
