package org.encalmo.tagstats;

import junit.framework.Assert;
import org.encalmo.actor.Callback;
import org.encalmo.nio.CallingThreadExecutor;
import org.encalmo.util.AssertThat;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class FileParserActorTest {

    public static final CallingThreadExecutor EXECUTOR = new CallingThreadExecutor();

    @Test
    public void shouldParseText1AndReturnTop10Tags() throws Exception {
        //given
        Path path1 = Paths.get("src/test/resources/text1.txt");
        final TagStats<String> s = new TagStatsSet<>(10, 1);
        TagParserStrategy ps = new GenericTagParserStrategy(5);
        TagParser p = new TagParserActor(new GenericTagParser(s, ps), EXECUTOR, 1);
        FileParser f = new FileParserActor(p, EXECUTOR, 1);
        Callback callback = new Callback() {
            @Override
            public void success() {
                //then
                Iterable<String> tags = s.top();
                AssertThat.sameElements(tags, TestData.EXPECTED_TOP10_TAGS_FROM_TEXT1);
            }

            @Override
            public void failure(Throwable cause) {
                //then
                Assert.fail(cause.getMessage());
            }
        };
        //when
        f.parse(path1, callback);
    }

    @Test
    public void shouldAsynchronouslyParseText1AndReturnTop10Tags() throws Exception {
        //given
        final SynchronousQueue<Boolean> hand = new SynchronousQueue<>();
        Path path1 = Paths.get("src/test/resources/text1.txt");
        final TagStats<String> s = new TagStatsSet<>(10, 1);
        TagParserStrategy ps = new GenericTagParserStrategy(5);
        TagParser p = new TagParserActor(new GenericTagParser(s, ps), Executors.newSingleThreadExecutor(), 1);
        FileParser f = new FileParserActor(p, Executors.newSingleThreadExecutor(), 1);
        Callback callback = new Callback() {
            @Override
            public void success() {
                //then
                Iterable<String> tags = s.top();
                AssertThat.sameElements(tags, TestData.EXPECTED_TOP10_TAGS_FROM_TEXT1);
                hand.offer(true);
            }

            @Override
            public void failure(Throwable cause) {
                //then
                hand.offer(false);
            }
        };
        //when
        f.parse(path1, callback);
        Boolean done = hand.poll(10, TimeUnit.SECONDS);
        Assert.assertNotNull(done);
        Assert.assertTrue(done.booleanValue());
    }

    @Test
    public void shouldFailBecauseOfFileNotReadable() throws Exception {
        //given
        final SynchronousQueue<Boolean> hand = new SynchronousQueue<>();
        Path path1 = Paths.get("src/test/resources/text1000.txt");
        final TagStats<String> s = new TagStatsSet<>(10, 1);
        TagParserStrategy ps = new GenericTagParserStrategy(5);
        TagParser p = new TagParserActor(new GenericTagParser(s, ps), Executors.newSingleThreadExecutor(), 10);
        FileParser f = new FileParserActor(p, Executors.newSingleThreadExecutor(), 10);
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
        f.parse(path1, callback);
        Boolean done = hand.poll(10, TimeUnit.SECONDS);
        Assert.assertNotNull(done);
        Assert.assertTrue(done.booleanValue());
    }

}
