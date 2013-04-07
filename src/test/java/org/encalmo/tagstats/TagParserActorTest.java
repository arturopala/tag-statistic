package org.encalmo.tagstats;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

public class TagParserActorTest {

    public static final CallingThreadExecutor EXECUTOR = new CallingThreadExecutor();

    @Test
    public void shouldParseString() {
        Reader r = new StringReader("kawa mleko\tkaw.a m./.leko,ka*wa\t\t\tm*L*e*k.o sok\t\r\r\"\" M?lekO,K1\tawa k1a\rwa k\n1awa k,a,wa\r\n\nk1awa m'leko9 ka_wa, m\"leko.9 heRbatA H.Erba/ta");
        TagStats<String> s = new TagStatsSet<>();
        TagParser p = new TagParserActor(new GenericTagParser(s), EXECUTOR, 1);
        p.parse(r);
        Iterable<String> tags = s.top();
        AssertThat.sameElements(tags, "mleko", "mleko9", "herbata", "k1awa");
    }

}
