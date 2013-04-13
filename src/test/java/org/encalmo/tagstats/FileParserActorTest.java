package org.encalmo.tagstats;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileParserActorTest {

    public static final CallingThreadExecutor EXECUTOR = new CallingThreadExecutor();

    @Test
    public void shouldParseText1AndReturnTop10Tags() throws Exception {
        //given
        Path path1 = Paths.get("src/test/resources/text1.txt");
        TagStats<String> s = new TagStatsSet<>();
        TagParserStrategy ps = new GenericTagParserStrategy(5);
        TagParser p = new TagParserActor(new GenericTagParser(s, ps), EXECUTOR, 1);
        FileParser f = new FileParserActor(p, EXECUTOR, 1);
        //when
        f.parse(path1);
        Iterable<String> tags = s.top();
        //then
        AssertThat.sameElements(tags, "ipsum", "adipiscing", "pellentesque", "tortor", "purus", "vitae", "ullamcorper", "dolor", "donec", "libero");
    }

}
