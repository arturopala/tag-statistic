package org.encalmo.tagstats;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagStatsServiceTest {

    @Test
    public void shouldParseFilesFromDirectoryAndReturnTop10Tags() throws Exception {
        //given
        TagStatsServiceConfig config = new TagStatsServiceConfig();
        config.setPort(33568);
        config.setDirectory("src/test/resources");
        TagStatsService service = new TagStatsService(config);
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

    @Test
    public void shouldParseFilesAndServeTop10RequestsInMultipleThreads() throws Exception {
        //given
        TagStatsServiceConfig config = new TagStatsServiceConfig();
        config.setPort(33568);
        final TagStatsService service = new TagStatsService(config);
        final InetSocketAddress address = service.getAddress();
        AssertThat.isEmpty(service.top());
        //when
        service.start();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    try {
                        service.parse(Paths.get("src/test/resources/text" + (i % 5 + 1) + ".txt"));
                    } catch (Exception e) {
                        Assert.fail(e.getMessage());
                    }
                }
            }
        });
        Thread.sleep(100);
        ExecutorService es = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            es.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TagStatsClient.readTop10TagsFromSocket(address, Charset.defaultCharset());
                    } catch (IOException e) {
                        Assert.fail(e.getMessage());
                    }
                }
            });
        }
        while (!service.isParsingQueueEmpty()) {
            Thread.sleep(1000);
        }
        //then
        AssertThat.sameElements(service.top(), TestData.EXPECTED_TOP10_TAGS_FROM_SRC_TEST_RESOURCES);
        String[] response = TagStatsClient.readTop10TagsFromSocket(address, Charset.defaultCharset());
        AssertThat.sameElements(response, TestData.EXPECTED_TOP10_TAGS_FROM_SRC_TEST_RESOURCES);
        service.stop();
    }

}
