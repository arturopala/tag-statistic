package org.encalmo.tagstats;

import org.junit.Test;

public class TagStatsActorTest {

    public static final CallingThreadExecutor EXECUTOR = new CallingThreadExecutor();

    @Test
    public void shouldAppendFirstTag() {
        TagStats<String> tags = new TagStatsActor<>(new TagStatsSet<String>(), EXECUTOR);
        AssertThat.sameElements(tags.top());
        tags.increment("bas");
        AssertThat.sameElements(tags.top(), "bas");
    }

    @Test
    public void shouldAppendManyTags() {
        TagStats<String> tags = new TagStatsActor<>(new TagStatsSet<String>(), EXECUTOR);
        AssertThat.sameElements(tags.top());
        tags.increment("bas");
        tags.increment("foo");
        tags.increment("foo");
        tags.increment("bar");
        tags.increment("foo");
        tags.increment("bar");
        AssertThat.sameElements(tags.top(), "foo", "bar", "bas");
    }

}
