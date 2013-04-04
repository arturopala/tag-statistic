package org.encalmo.tagstatistic;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

public class GenericTagParserTest {

    @Test
    public void shouldParseString(){
        Reader r = new StringReader("kawa mleko\tkaw.a m./.leko ka-wa\t\t\tm-L-e-k.o sok\t\r\r\"\" M?lekO K1\tawa k1a\rwa k\n1awa k,a,wa\r\n\nk1awa m'leko9 ka_wa, m\"leko.9 heRbatA H.Erba/ta");
        TagParser p = new GenericTagParser();
        TagStatistic<String> s = new TagStatisticSet<>();
        p.parse(r,s);
        Iterable<String> tags = s.top();
        AssertThat.sameElements(tags,"mleko","mleko9","herbata","k1awa");
    }

}
