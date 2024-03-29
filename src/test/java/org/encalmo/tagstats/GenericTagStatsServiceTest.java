package org.encalmo.tagstats;

import junit.framework.Assert;
import org.encalmo.actor.Callback;
import org.encalmo.util.AssertThat;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

public class GenericTagStatsServiceTest {

    @Test
    public void shouldParseFilesFromDirectoryAndReturnTop10Tags() throws Exception {
        //given
        TagStatsServiceConfig config = new TagStatsServiceConfig();
        config.setServerSocketPort(33568);
        config.setBaseDirectory("src/test/resources");
        GenericTagStatsService service = TagStatsServiceFactory.createAsynchronous(config);
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
        final SynchronousQueue<Boolean> hand = new SynchronousQueue<>();
        TagStatsServiceConfig config = new TagStatsServiceConfig();
        config.setServerSocketPort(33568);
        final GenericTagStatsService service = TagStatsServiceFactory.createAsynchronous(config);
        final InetSocketAddress address = service.getAddress();
        AssertThat.isEmpty(service.top());
        final Callback callback = new Callback() {
            @Override
            public void success() {
                //then
                try {
                    AssertThat.sameElements(service.top(), TestData.EXPECTED_TOP10_TAGS_FROM_SRC_TEST_RESOURCES);
                    String[] response = TagStatsClient.readTop10TagsFromSocket(address, Charset.defaultCharset());
                    AssertThat.sameElements(response, TestData.EXPECTED_TOP10_TAGS_FROM_SRC_TEST_RESOURCES);
                    service.stop();
                    hand.offer(true);
                } catch (Exception e) {
                    hand.offer(false);
                }
            }

            @Override
            public void failure(Throwable cause) {
                hand.offer(false);
            }
        };
        //when
        service.start();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 999; i++) {
                    try {
                        service.parse(Paths.get("src/test/resources/text" + (i % 5 + 1) + ".txt"), Callback.EMPTY);
                    } catch (Exception e) {
                        hand.offer(false);
                    }
                }
                try {
                    service.parse(Paths.get("src/test/resources/text5.txt"), callback);
                } catch (Exception e) {
                    hand.offer(false);
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
        Boolean done = hand.poll(10, TimeUnit.SECONDS);
        assertNotNull(done);
    }

}
