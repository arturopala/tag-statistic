package org.encalmo.tagstats;

import junit.framework.Assert;
import org.junit.Test;

public class TagStatsServiceConfigTest {

    @Test
    public void testSettersAndGetters() {
        //given
        int port = 8080;
        String path = "foo/bar/fas";
        int threads = 10;
        int size = 25;
        int accuracy = 50;
        GenericTagParserStrategy strategy = new GenericTagParserStrategy(10);
        TagStatsServiceConfig c = new TagStatsServiceConfig();
        //when
        c.setServerSocketPort(port);
        c.setBaseDirectory(path);
        c.setNumberOfThreads(threads);
        c.setTagParserStrategy(strategy);
        c.setTopListSize(size);
        c.setTopListUpdateAccuracy(accuracy);
        //then
        Assert.assertEquals(port, c.getServerSocketPort());
        Assert.assertEquals(path, c.getBaseDirectory());
        Assert.assertEquals(threads, c.getNumberOfThreads());
        Assert.assertEquals(size, c.getTopListSize());
        Assert.assertEquals(strategy, c.getTagParserStrategy());
        Assert.assertEquals(accuracy, c.getTopListUpdateAccuracy());
    }

    @Test
    public void testConstructorAndGetters() {
        //given
        int port = 8080;
        String path = "foo/bar/fas";
        int threads = 10;
        int size = 25;
        int accuracy = 50;
        GenericTagParserStrategy strategy = new GenericTagParserStrategy(10);
        //when
        TagStatsServiceConfig c = new TagStatsServiceConfig(port, path, threads, strategy, size, accuracy);
        //then
        Assert.assertEquals(port, c.getServerSocketPort());
        Assert.assertEquals(path, c.getBaseDirectory());
        Assert.assertEquals(threads, c.getNumberOfThreads());
        Assert.assertEquals(size, c.getTopListSize());
        Assert.assertEquals(strategy, c.getTagParserStrategy());
        Assert.assertEquals(accuracy, c.getTopListUpdateAccuracy());
    }
}
