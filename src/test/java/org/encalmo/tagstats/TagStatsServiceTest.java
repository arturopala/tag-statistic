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

public class TagStatsServiceTest {
    @Test
    public void shouldParseFilesFromDirectoryAndReturnTop10Tags() throws Exception {
        //given
        String[] expectedTopTags = new String[]{
                "ipsum", "vitae", "nulla", "pellentesque", "vestibulum", "mauris", "sapien",
                "aliquam", "tortor", "dolor"
        };
        String expectedResponse = prepareExpectedResponse(expectedTopTags);
        TagStatsServiceConfig config = new TagStatsServiceConfig();
        config.setPort(33568);
        config.setDirectory("src/test/resources");
        config.setThreads(3);
        TagStatsService service = new TagStatsService(config);
        AssertThat.sameElements(service.top());
        //when
        service.start();
        Thread.sleep(1000);
        //then
        AssertThat.sameElements(service.top(), expectedTopTags);
        InetSocketAddress address = service.getAddress();
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
        return charBuffer.toString();
    }

}
