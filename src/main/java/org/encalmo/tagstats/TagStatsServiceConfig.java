package org.encalmo.tagstats;

/**
 * Configuration parameters of the TagStatsService
 */
public class TagStatsServiceConfig {

    private int port;
    private String directory;
    private int threads = Runtime.getRuntime().availableProcessors() * 2;
    private TagParseStrategy tagParseStrategy = new GenericTagParseStrategy(5);

    public TagStatsServiceConfig(int port, String directory, int threads, TagParseStrategy tagParseStrategy) {
        this.port = port;
        this.directory = directory;
        this.threads = threads;
        this.tagParseStrategy = tagParseStrategy;
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

    public TagParseStrategy getTagParseStrategy() {
        return tagParseStrategy;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setTagParseStrategy(TagParseStrategy tagParseStrategy) {
        this.tagParseStrategy = tagParseStrategy;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
}
