package org.encalmo.tagstatistic;

import org.junit.Assert;
import org.junit.Test;


public class TagStatisticSetTest {
  @Test
  public void shouldAppendFirstTag() {
    TagStatistic<String> tags = new TagStatisticSet<>();
    tags.increment("bas");
    AssertThat.sameElements(tags.top(), "bas");
  }

  @Test
  public void shouldAppendManyTags() {
    TagStatistic<String> tags = new TagStatisticSet<>();
    tags.increment("bas");
    tags.increment("foo");
    tags.increment("foo");
    tags.increment("bar");
    tags.increment("foo");
    tags.increment("bar");
    AssertThat.sameElements(tags.top(), "foo", "bar", "bas");
  }

  @Test
  public void shouldCalculateTagShare() {
    TagStatisticSet<String> tags = new TagStatisticSet<>();
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
    AssertThat.sameElements(tags.top(), "bar", "foo", "bas");
    Assert.assertEquals(0.5, tags.shareOf("bar"), 0.01);
    Assert.assertEquals(0.3, tags.shareOf("foo"), 0.01);
    Assert.assertEquals(0.2, tags.shareOf("bas"), 0.01);
    Assert.assertEquals(0, tags.shareOf("null"), 0.01);
  }


}
