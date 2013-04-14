package org.encalmo.tagstats;

import org.encalmo.util.AssertThat;
import org.junit.Assert;
import org.junit.Test;


public class TagStatsSetTest {
    @Test
    public void shouldAppendTagAndIncrementRank() {
        //given
        TagStatsSet<String> tags = new TagStatsSet<>(10, 1);
        //when
        tags.increment("bas");
        //then
        AssertThat.sameElements(tags.top(), "bas");
    }

    @Test
    public void shouldAppendTagsAndReturnTopTags() {
        //given
        TagStatsSet<String> tags = new TagStatsSet<>(10, 1);
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

    @Test
    public void shouldCalculateTagShare() {
        //given
        TagStatsSet<String> tags = new TagStatsSet<>(10, 1);
        //when
        tags.increment("bas");
        tags.increment("bas");
        tags.increment("foo");
        tags.increment("bar");
        tags.increment("foo");
        tags.increment("bar");
        tags.increment("foo");
        tags.increment("bar");
        tags.increment("bar");
        tags.increment("bar");
        //then
        AssertThat.sameElements(tags.top(), "bar", "foo", "bas");
        Assert.assertEquals(0.5, tags.shareOf("bar"), 0.01);
        Assert.assertEquals(0.3, tags.shareOf("foo"), 0.01);
        Assert.assertEquals(0.2, tags.shareOf("bas"), 0.01);
        Assert.assertEquals(0, tags.shareOf("null"), 0.01);
    }


}
