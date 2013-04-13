package org.encalmo.tagstats;

public class TestData {

    static final String TEST_STRING_1 = "kawa mleko\tkaw.a,m./.leko ka+wa\t\t\tm+L+e+k.o sok\t\r\r\"\" M?lekO K1\tawa k1a\rwa k\n1awa k,a,wa\r\n\nk1awa m'leko9 ka_wa, m\"leko.9 heRbatA,H.Erba/ta";
    static final String[] EXPECTED_TOP10_TAGS_1 = new String[]{"mleko", "mleko9", "herbata", "k1awa"};

    static final String[] EXPECTED_TOP10_TAGS_FROM_SRC_TEST_RESOURCES = new String[]{
            "ipsum", "vitae", "nulla", "pellentesque", "vestibulum", "mauris", "sapien",
            "aliquam", "tortor", "dolor"
    };
}
