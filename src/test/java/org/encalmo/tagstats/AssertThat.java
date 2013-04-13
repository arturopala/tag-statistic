package org.encalmo.tagstats;

import org.junit.Assert;

public class AssertThat {

    public static void sameElements(Iterable<String> tags, String... expected) {
        int i = 0;
        for (String tag : tags) {
            Assert.assertEquals(expected[i], tag);
            i++;
        }
    }

    public static void isEmpty(Iterable<String> tags) {
        int i = 0;
        for (String tag : tags) {
            i++;
        }
    }

}
