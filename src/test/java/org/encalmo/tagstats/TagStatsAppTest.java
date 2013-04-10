package org.encalmo.tagstats;

import junit.framework.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


public class TagStatsAppTest {
    @Test
    public void shouldParseFilesFromDirectoryAndReturnTopTags() throws Exception {
        String[] expectedTopTags = new String[]{
                "ipsum", "vitae", "nulla", "pellentesque", "vestibulum", "mauris", "sapien",
                "aliquam", "tortor", "dolor"
        };
        StringBuffer er = new StringBuffer();
        for (String tag : expectedTopTags) {
            er.append(tag);
            er.append("\n");
        }

        String expectedResponse = er.toString();
        TagStatsApp app = new TagStatsApp(33568, "src/test/resources", 3);
        AssertThat.sameElements(app.top());
        app.start();
        Thread.sleep(1000);
        AssertThat.sameElements(app.top(), expectedTopTags);

        SocketChannel channel = SocketChannel.open();
        if (!channel.connect(app.getAddress())) {
            channel.finishConnect();
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        buffer.flip();

        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        CharBuffer charBuffer = decoder.decode(buffer);
        String response = charBuffer.toString();
        Assert.assertEquals(expectedResponse, response);
        app.stop();
        channel.close();
    }

}
