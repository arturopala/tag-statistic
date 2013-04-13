package org.encalmo.tagstats;

/**
 * Created with IntelliJ IDEA.
 * User: artur
 * Date: 13.04.13
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */
public class TestData {
    static final String TEST_STRING_1 = "kawa mleko\tkaw.a,m./.leko ka+wa\t\t\tm+L+e+k.o sok\t\r\r\"\" M?lekO K1\tawa k1a\rwa k\n1awa k,a,wa\r\n\nk1awa m'leko9 ka_wa, m\"leko.9 heRbatA,H.Erba/ta";
    static final String[] EXPECTED_TOP10_TAGS_1 = new String[]{"mleko", "mleko9", "herbata", "k1awa"};
}
