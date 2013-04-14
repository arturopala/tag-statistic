package org.encalmo.util;

import org.junit.Assert;

public class AssertThat {

    public static void sameElements(Iterable<String> iterable, String... expected) {
        int i = 0;
        for (String string : iterable) {
            Assert.assertEquals(expected[i], string);
            i++;
        }
    }

    public static void isEmpty(Iterable<String> iterable) {
        int i = 0;
        for (String string : iterable) {
            i++;
        }
    }


    public static void sameElements(String[] actual, String[] expected) {
        Assert.assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }
}
