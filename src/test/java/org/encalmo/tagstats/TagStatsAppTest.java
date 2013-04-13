package org.encalmo.tagstats;

import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;

public class TagStatsAppTest {

    @Test
    public void testParseArguments() throws Exception {
        //when
        TagStatsServiceConfig config = TagStatsApp.parseArguments("-p 8080 -d src/test/resources -t 6 -s 25 -a 5".split(" "));
        //then
        Assert.assertEquals(8080, config.getServerSocketPort());
        Assert.assertEquals("src/test/resources", config.getBaseDirectory());
        Assert.assertEquals(6, config.getNumberOfThreads());
        Assert.assertEquals(25, config.getTopListSize());
        Assert.assertEquals(5, config.getTopListUpdateAccuracy());
    }

    @Test
    public void testStart() throws Exception {
        //given
        TagStatsServiceConfig config = new TagStatsServiceConfig();
        config.setServerSocketPort(33568);
        config.setBaseDirectory("src/test/resources");
        config.setTopListUpdateAccuracy(10);
        TagStatsService service = TagStatsApp.start(config);
        AssertThat.isEmpty(service.top());
        //when
        service.start();
        Thread.sleep(1000);
        //then
        AssertThat.sameElements(service.top(), TestData.EXPECTED_TOP10_TAGS_FROM_SRC_TEST_RESOURCES);
        InetSocketAddress address = service.getAddress();
        String[] response = TagStatsClient.readTop10TagsFromSocket(address, java.nio.charset.Charset.defaultCharset());
        AssertThat.sameElements(response, TestData.EXPECTED_TOP10_TAGS_FROM_SRC_TEST_RESOURCES);
        service.stop();
    }
}
