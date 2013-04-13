package org.encalmo.tagstats;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagStatsServiceTest {

    private static final String[] EXPECTED_TOP_TAGS = new String[]{
            "ipsum", "vitae", "nulla", "pellentesque", "vestibulum", "mauris", "sapien",
            "aliquam", "tortor", "dolor"
    };

    @Test
    public void shouldParseFilesFromDirectoryAndReturnTop10Tags() throws Exception {
        //given
        String expectedResponse = prepareExpectedResponse(EXPECTED_TOP_TAGS);
        TagStatsServiceConfig config = new TagStatsServiceConfig();
        config.setPort(33568);
        config.setDirectory("src/test/resources");
        TagStatsService service = new TagStatsService(config);
        AssertThat.isEmpty(service.top());
        //when
        service.start();
        Thread.sleep(1000);
        //then
        AssertThat.sameElements(service.top(), EXPECTED_TOP_TAGS);
        InetSocketAddress address = service.getAddress();
        String response = readTop10TagsFromSocket(address);
        Assert.assertEquals(expectedResponse, response);
        service.stop();
    }

    @Test
    public void shouldParseFilesAndServeTop10RequestsInMultipleThreads() throws Exception {
        //given
        String expectedResponse = prepareExpectedResponse(EXPECTED_TOP_TAGS);
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
                        readTop10TagsFromSocket(address);
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
        AssertThat.sameElements(service.top(), EXPECTED_TOP_TAGS);
        String response = readTop10TagsFromSocket(address);
        Assert.assertEquals(expectedResponse, response);
        service.stop();
    }

    private String prepareExpectedResponse(String[] expectedTopTags) {
        StringBuffer er = new StringBuffer();
        for (String tag : expectedTopTags) {
            er.append(tag);
            er.append("\n");
        }

        return er.toString();
    }

    private String readTop10TagsFromSocket(InetSocketAddress address) throws IOException {
        SocketChannel channel = SocketChannel.open();
        if (!channel.connect(address)) {
            channel.finishConnect();
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        channel.close();
        buffer.flip();
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        CharBuffer charBuffer = decoder.decode(buffer);
        String response = charBuffer.toString();
        System.out.println("[" + Thread.currentThread().getName() + "] response received " + response.length());
        return response;
    }

}
