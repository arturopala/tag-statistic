package org.encalmo.tagstats;

/**
 * Configuration parameters of the {@link TagStatsService}
 */
public class TagStatsServiceConfig {

    private int port;
    private String directory;
    private int threads = Runtime.getRuntime().availableProcessors() * 2;
    private TagParserStrategy tagParserStrategy = new GenericTagParserStrategy(5);

    public TagStatsServiceConfig(int port, String directory, int threads, TagParserStrategy tagParserStrategy) {
        this.port = port;
        this.directory = directory;
        this.threads = threads;
        this.tagParserStrategy = tagParserStrategy;
    }

    public TagStatsServiceConfig() {
    }

    /**
     * Local server socket port number or -1 if server not enabled
     */
    public int getPort() {
        return port;
    }

    /**
     * Path to the directory to watch or null if not enabled
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Number of threads for parallel parsing, default <code>Runtime.getRuntime().availableProcessors() * 2</code>
     */
    public int getThreads() {
        return threads;
    }

    /**
     * Strategy of the tag parser, default {@link GenericTagParserStrategy}
     */
    public TagParserStrategy getTagParserStrategy() {
        return tagParserStrategy;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setTagParserStrategy(TagParserStrategy tagParserStrategy) {
        this.tagParserStrategy = tagParserStrategy;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
}
