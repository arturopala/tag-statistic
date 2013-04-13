package org.encalmo.tagstats;

/**
 * Configuration parameters of the TagStatsService
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

    public int getPort() {
        return port;
    }

    public String getDirectory() {
        return directory;
    }

    public int getThreads() {
        return threads;
    }

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
