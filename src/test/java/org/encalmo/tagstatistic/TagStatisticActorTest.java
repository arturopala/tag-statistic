package org.encalmo.tagstatistic;

import org.junit.Assert;
import org.junit.Test;


public class TagStatisticActorTest {
  @Test
  public void shouldAppendFirstTag() {
    TagStatistic<String> tags = new TagStatisticActor<>();
    tags.increment("bas");
    AssertThat.sameElements(tags.top());
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
    }
    AssertThat.sameElements(tags.top(), "bas");
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
    AssertThat.sameElements(tags.top());
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
    }
    AssertThat.sameElements(tags.top(), "foo", "bar", "bas");
  }

}
