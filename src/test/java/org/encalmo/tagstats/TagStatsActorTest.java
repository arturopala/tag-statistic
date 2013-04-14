package org.encalmo.tagstats;

import org.encalmo.nio.CallingThreadExecutor;
import org.encalmo.util.AssertThat;
import org.junit.Test;

public class TagStatsActorTest {

    public static final CallingThreadExecutor EXECUTOR = new CallingThreadExecutor();

    @Test
    public void shouldAppendTagAndIncrementRank() {
        //given
        TagStats<String> tags = new TagStatsActor<>(new TagStatsSet<String>(10, 1), EXECUTOR);
        AssertThat.sameElements(tags.top());
        //when
        tags.increment("bas");
        //then
        AssertThat.sameElements(tags.top(), "bas");
    }

    @Test
    public void shouldAppendTagsAndReturnTopTags() {
        //given
        TagStats<String> tags = new TagStatsActor<>(new TagStatsSet<String>(10, 1), EXECUTOR);
        AssertThat.sameElements(tags.top());
        //when
        tags.increment("bas");
        tags.increment("foo");
        tags.increment("foo");
        tags.increment("bar");
        tags.increment("foo");
        tags.increment("bar");
        //then
        AssertThat.sameElements(tags.top(), "foo", "bar", "bas");
    }

}
