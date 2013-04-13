package org.encalmo.tagstats;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

public class TagParserActorTest {

    public static final CallingThreadExecutor EXECUTOR = new CallingThreadExecutor();

    @Test
    public void shouldParseStringAndReturnTop10Tags() {
        //given
        Reader r = new StringReader(TestData.TEST_STRING_1);
        TagStats<String> s = new TagStatsSet<>();
        TagParser p = new TagParserActor(new GenericTagParser(s), EXECUTOR, 1);
        //when
        p.parse(r);
        Iterable<String> tags = s.top();
        //then
        AssertThat.sameElements(tags, TestData.EXPECTED_TOP10_TAGS_1);
    }

}
