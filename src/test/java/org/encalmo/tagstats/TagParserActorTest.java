package org.encalmo.tagstats;

import junit.framework.Assert;
import org.encalmo.actor.Callback;
import org.encalmo.nio.CallingThreadExecutor;
import org.encalmo.util.AssertThat;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class TagParserActorTest {

    public static final CallingThreadExecutor EXECUTOR = new CallingThreadExecutor();

    @Test
    public void shouldParseStringAndReturnTop10Tags() throws Exception {
        //given
        Reader r = new StringReader(TestData.TEST_STRING_1);
        final TagStats<String> s = new TagStatsSet<>(10, 1);
        TagParserStrategy ps = new GenericTagParserStrategy(5);
        TagParser p = new TagParserActor(new GenericTagParser(s, ps), EXECUTOR, 1);
        Callback callback = new Callback() {
            @Override
            public void success() {
                //then
                Iterable<String> tags = s.top();
                AssertThat.sameElements(tags, TestData.EXPECTED_TOP10_TAGS_1);
            }

            @Override
            public void failure(Throwable cause) {
                //then
                Assert.fail(cause.getMessage());
            }
        };
        //when
        p.parse(r, callback);
    }

    @Test
    public void shouldAsynchronouslyParseStringAndReturnTop10Tags() throws Exception {
        //given
        final SynchronousQueue<Boolean> hand = new SynchronousQueue<>();
        Reader r = new StringReader(TestData.TEST_STRING_1);
        final TagStats<String> s = new TagStatsSet<>(10, 1);
        TagParserStrategy ps = new GenericTagParserStrategy(5);
        TagParser p = new TagParserActor(new GenericTagParser(s, ps), Executors.newSingleThreadExecutor(), 1);
        Callback callback = new Callback() {
            @Override
            public void success() {
                //then
                Iterable<String> tags = s.top();
                AssertThat.sameElements(tags, TestData.EXPECTED_TOP10_TAGS_1);
                hand.offer(true);
            }

            @Override
            public void failure(Throwable cause) {
                //then
                hand.offer(false);
            }
        };
        //when
        p.parse(r, callback);
        Boolean done = hand.poll(10, TimeUnit.SECONDS);
        Assert.assertNotNull(done);
        Assert.assertTrue(done.booleanValue());
    }

    @Test
    public void shouldFailBecauseOfClosedStream() throws Exception {
        //given
        final SynchronousQueue<Boolean> hand = new SynchronousQueue<>();
        Reader r = new StringReader(TestData.TEST_STRING_1);
        final TagStats<String> s = new TagStatsSet<>(10, 1);
        TagParserStrategy ps = new GenericTagParserStrategy(5);
        TagParser p = new TagParserActor(new GenericTagParser(s, ps), Executors.newSingleThreadExecutor(), 1);
        Callback callback = new Callback() {
            @Override
            public void success() {
                //then
                hand.offer(false);
            }

            @Override
            public void failure(Throwable cause) {
                //then
                hand.offer(true);
            }
        };
        //when
        r.close();
        p.parse(r, callback);
        Boolean done = hand.poll(10, TimeUnit.SECONDS);
        Assert.assertNotNull(done);
        Assert.assertTrue(done.booleanValue());
    }

}
