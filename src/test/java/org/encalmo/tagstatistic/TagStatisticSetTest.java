package org.encalmo.tagstatistic;

import org.junit.Assert;
import org.junit.Test;


public class TagStatisticSetTest {
  @Test
  public void shouldAppendFirstTag() {
    TagStatistic<String> tags = new TagStatisticSet<>();
    tags.increment("bas");
    assertSame(tags.top(), "bas");
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
    assertSame(tags.top(), "foo", "bar", "bas");
  }

  @Test
  public void shouldCalculateTagShare() {
    TagStatistic<String> tags = new TagStatisticSet<>();
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
    assertSame(tags.top(), "bar", "foo", "bas");
    Assert.assertEquals(0.5, tags.shareOf("bar"), 0.01);
    Assert.assertEquals(0.3, tags.shareOf("foo"), 0.01);
    Assert.assertEquals(0.2, tags.shareOf("bas"), 0.01);
    Assert.assertEquals(0, tags.shareOf("null"), 0.01);
  }

  private void assertSame(Iterable<String> tags, String... expected) {
    int i = 0;
    for (String tag : tags) {
      Assert.assertEquals(expected[i], tag);
      i++;
    }
  }


}
