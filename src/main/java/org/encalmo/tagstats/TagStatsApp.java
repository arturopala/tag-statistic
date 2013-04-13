package org.encalmo.tagstats;

import java.util.Properties;


/**
 * {@link TagStatsService} launcher.
 */
public class TagStatsApp {

    private static final String USAGE_MESSAGE = "Usage: java -cp {tagstats.jar} " + TagStatsApp.class.getName() + " -p {port} -d {baseDirectory} [-t {numberOfThreads (1,50)} -s {topListSize (1,100)} -a {topListUpdateAccuracy (1,100)}]";

    private TagStatsApp() {
    }

    public static void main(String[] args) throws Exception {
        //parse arguments
        TagStatsServiceConfig config = parseArguments(args);
        //create and start service
        start(config);
        //wait forever
        for (; ; ) {
            Thread.sleep(60000);
        }
    }

    protected static TagStatsServiceConfig parseArguments(String[] args) {
        if (args.length >= 4) {
            Properties p = new Properties();
            p.setProperty(args[0], args[1]);
            p.setProperty(args[2], args[3]);
            if (args.length >= 6) {
                p.setProperty(args[4], args[5]);
                if (args.length >= 8) {
                    p.setProperty(args[6], args[7]);
                    if (args.length >= 10) {
                        p.setProperty(args[8], args[9]);
                    }
                }
            }
            int port = Integer.parseInt(p.getProperty("-p"));
            String directory = p.getProperty("-d");
            int threads = Integer.parseInt(p.getProperty("-t", "0"));
            int topListSize = Integer.parseInt(p.getProperty("-s", "10"));
            int accuracy = Integer.parseInt(p.getProperty("-a", "1"));
            return new TagStatsServiceConfig(port, directory, threads, new GenericTagParserStrategy(5), topListSize, accuracy);
        } else {
            throw new AssertionError(USAGE_MESSAGE);
        }
    }

    protected static TagStatsService start(TagStatsServiceConfig config) throws Exception {
        TagStatsService service = new TagStatsService(config);
        service.start();
        return service;
    }
}
