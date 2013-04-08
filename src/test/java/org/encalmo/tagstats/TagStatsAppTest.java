package org.encalmo.tagstats;

import org.junit.Test;


public class TagStatsAppTest {
  @Test
  public void shouldParseFilesFromDirectoryAndReturnTopTags() throws Exception {
    TagStatsApp app = new TagStatsApp(33568, "src/test/resources", 5);
    app.start();
    Thread.sleep(3000);
    AssertThat.sameElements(app.top(), "ipsum", "vitae", "nulla", "pellentesque", "vestibulum", "mauris", "sapien",
      "aliquam", "tortor", "dolor");
    app.stop();
  }

}
