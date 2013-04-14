package org.encalmo.tagstats;

import org.encalmo.util.ManageableService;

import java.util.Properties;


/**
 * “Tag Stats” app reading and parsing in parallel multiple text files and finding the TopN frequently occurring words (tags).
 * The app maintain a real time in-memory version of the Top 10 Tag list.
 * The user can query the real time version of the list thorough a socket connection (if enabled).
 * The app scans given directory for existing and new text files  (if enabled).
 * <p/>
 * Default features:
 * <ul>
 * <li> The app processes multiple files at the same time.
 * <li> Tags are composed by a mix of letters and numbers and dash “-“. (Eg: 12313, abc123, 66-route, 66route,).
 * <li> Tags can also be numbers or a mix of letters and numbers. (Eg: 12313, abc123, 66route).
 * <li> The plural version of a word are counted as a separate tag. Eg: Bottle and Bottles are two different tags.
 * <li> Symbols, Space or punctuation marks are counted as tags.
 * <li> New lines, carriage returns, tabs and any white space are excluded.
 * <li> When the application completes processing it writes to standard output the top 10 tag list (neatly formatted)
 * <li> sorted in descending order based on number of occurrences.
 * <li> The application accepts multiple connections on port specified (as a command line parameter, -p).
 * <li> When the connection is made, the application prints the top 10 tag list (line by line) sorted in descending
 * <li> order based on the number of occurrences and closes the connection immediately.
 * </ul>
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
        TagStatsService service = TagStatsServiceFactory.createAsynchronous(config);
        if (service instanceof ManageableService) {
            ((ManageableService) service).start();
        }
        return service;
    }
}
