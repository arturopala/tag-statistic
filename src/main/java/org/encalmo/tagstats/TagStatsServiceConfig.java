package org.encalmo.tagstats;

/**
 * Configuration parameters of the {@link TagStatsService}
 */
public class TagStatsServiceConfig {

    private int serverSocketPort;
    private String baseDirectory;
    private int numberOfThreads = Runtime.getRuntime().availableProcessors() * 2;
    private TagParserStrategy tagParserStrategy = new GenericTagParserStrategy(5);
    private int topListSize = 10;
    private int topListUpdateAccuracy = 1;

    public TagStatsServiceConfig(int serverSocketPort, String baseDirectory, int numberOfThreads, TagParserStrategy tagParserStrategy, int topListSize, int topListUpdateAccuracy) {
        this.serverSocketPort = serverSocketPort;
        this.baseDirectory = baseDirectory;
        this.numberOfThreads = numberOfThreads;
        this.tagParserStrategy = tagParserStrategy;
        this.topListSize = topListSize;
        this.topListUpdateAccuracy = topListUpdateAccuracy;
    }

    public TagStatsServiceConfig() {
    }

    /**
     * Local server socket port number or -1 if server not enabled
     */
    public int getServerSocketPort() {
        return serverSocketPort;
    }

    /**
     * Path to the baseDirectory to watch or null if not enabled
     */
    public String getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Number of threads for parallel parsing, default <code>Runtime.getRuntime().availableProcessors() * 2</code>
     */
    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    /**
     * Strategy of the tag parser, default {@link GenericTagParserStrategy}
     */
    public TagParserStrategy getTagParserStrategy() {
        return tagParserStrategy;
    }

    /**
     * Size of the TopN tag list
     */
    public int getTopListSize() {
        return topListSize;
    }

    /**
     * At how many increment steps {@link TagStatsSet} should update the top list
     */
    public int getTopListUpdateAccuracy() {
        return topListUpdateAccuracy;
    }

    public void setServerSocketPort(int serverSocketPort) {
        this.serverSocketPort = serverSocketPort;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public void setTagParserStrategy(TagParserStrategy tagParserStrategy) {
        this.tagParserStrategy = tagParserStrategy;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public void setTopListSize(int topListSize) {
        this.topListSize = topListSize;
    }

    public void setTopListUpdateAccuracy(int topListUpdateAccuracy) {
        this.topListUpdateAccuracy = topListUpdateAccuracy;
    }

}
