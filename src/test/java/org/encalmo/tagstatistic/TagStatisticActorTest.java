package org.encalmo.tagstatistic;

import org.junit.Assert;
import org.junit.Test;


public class TagStatisticActorTest {
  @Test
  public void shouldAppendFirstTag() {
    TagStatistic<String> tags = new TagStatisticActor<>();
    tags.increment("bas");
    assertSame(tags.top());
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
    }
    assertSame(tags.top(), "bas");
  }

  @Test
  public void shouldAppendManyTags() {
    TagStatistic<String> tags = new TagStatisticActor<>();
    tags.increment("bas");
    tags.increment("foo");
    tags.increment("foo");
    tags.increment("bar");
    tags.increment("foo");
    tags.increment("bar");
    assertSame(tags.top());
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
    }
    assertSame(tags.top(), "foo", "bar", "bas");
  }

  private void assertSame(Iterable<String> tags, String... expected) {
    int i = 0;
    for (String tag : tags) {
      Assert.assertEquals(expected[i], tag);
      i++;
    }
    Assert.assertEquals(expected.length, i);
  }
}
