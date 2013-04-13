package org.encalmo.tagstats;

import java.util.Properties;


/**
 * {@link TagStatsService} launcher.
 */
public class TagStatsApp {

    public static void main(String[] args) throws Exception {
        if (args.length >= 4) {
            //parse arguments
            Properties p = new Properties();
            p.setProperty(args[0], args[1]);
            p.setProperty(args[2], args[3]);
            if (args.length >= 6) {
                p.setProperty(args[4], args[5]);
            }
            int port = Integer.parseInt(p.getProperty("-p"));
            String directory = p.getProperty("-d");
            int threads = Integer.parseInt(p.getProperty("-t", "0"));
            //create and start service
            TagStatsServiceConfig config = new TagStatsServiceConfig(port, directory, threads, new GenericTagParseStrategy(5));
            TagStatsService service = new TagStatsService(config);
            service.start();
            //wait forever
            for (; ; ) {
                Thread.sleep(60000);
            }
        } else {
            throw new AssertionError("Usage: java -cp {tagstats.jar} " + TagStatsApp.class.getName() +
                    " -p {port} -d {base directory} [-t {threads}]");
        }
    }
}
