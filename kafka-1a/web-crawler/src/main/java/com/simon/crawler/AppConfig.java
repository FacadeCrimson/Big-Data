package com.simon.crawler;

import java.io.File;
import java.util.ArrayList;

public class AppConfig {

    public AppConfig() {
    }

    /**
     * 
     */
    private String topic;

    /**
     * 
     */
    private String bootstrap_servers = "127.0.0.1:9092";

    /**
     * 
     */
    private String crawl_storage = "/Users/apple/Desktop/data/";

    /**
     * 
     */
    private ArrayList<String> seeds;

    /**
     * 
     */
    private ArrayList<String> prefixes;

    /**
     * Validates the configs specified by this instance.
     *
     * @throws Exception on Validation fail
     */
    public void validate() throws Exception {
        if (topic == null) {
            throw new Exception("Please set kafka topic.");
        }
        if (!new File(crawl_storage).exists()) {
            throw new Exception("Directory doesn't exist.");
        }
        if (seeds == null) {
            throw new Exception("Please add least one seed.");
        }
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBootstrapServers() {
        return this.bootstrap_servers;
    }

    public void setBootstrapServers(String bootstrap_servers) {
        this.bootstrap_servers = bootstrap_servers;
    }

    public String getCrawlStorage() {
        return this.crawl_storage;
    }

    public void setCrawlStorage(String crawl_storage) {
        this.crawl_storage = crawl_storage;
    }

    public ArrayList<String> getSeeds() {
        return this.seeds;
    }

    public void addSeeds(String newseed) {
        if (this.seeds == null) {
            this.seeds = new ArrayList<String>();
        }
        this.seeds.add(newseed);
    }

    public ArrayList<String> getPrefixes() {
        return this.prefixes;
    }

    public void addPrefixes(String newprefix) {
        if (this.prefixes == null) {
            this.prefixes = new ArrayList<String>();
        }
        this.prefixes.add(newprefix);
    }

    // /**
    // * Sets the folder which will be used by crawler for storing the
    // * intermediate crawl data (e.g. list of urls that are extracted
    // * from previously fetched pages and need to be crawled later).
    // * Content of this folder should not be modified manually.
    // */
    // public void setCrawlStorageFolder(String crawlStorageFolder) {
    // this.crawlStorageFolder = crawlStorageFolder;
    // }

    // /**
    // * Only used if {@link #setOnlineTldListUpdate(boolean)} is {@code true}. If
    // * this property is not null then it overrides
    // * {@link #setPublicSuffixSourceUrl(String)}
    // *
    // * @param publicSuffixLocalFile local filename of public suffix list
    // */
    // public void setPublicSuffixLocalFile(String publicSuffixLocalFile) {
    // this.publicSuffixLocalFile = publicSuffixLocalFile;
    // // }
    // @Override
    // public String toString() {
    // StringBuilder sb = new StringBuilder();
    // sb.append("Crawl storage folder: " + getCrawlStorageFolder() + "\n");
    // sb.append("Resumable crawling: " + isResumableCrawling() + "\n");
    // sb.append("Max depth of crawl: " + getMaxDepthOfCrawling() + "\n");
    // sb.append("Max pages to fetch: " + getMaxPagesToFetch() + "\n");
    // sb.append("User agent string: " + getUserAgentString() + "\n");
    // sb.append("Include https pages: " + isIncludeHttpsPages() + "\n");
    // sb.append("Include binary content: " + isIncludeBinaryContentInCrawling() +
    // "\n");
    // sb.append("Max connections per host: " + getMaxConnectionsPerHost() + "\n");
    // sb.append("Max total connections: " + getMaxTotalConnections() + "\n");
    // sb.append("Socket timeout: " + getSocketTimeout() + "\n");
    // sb.append("Max total connections: " + getMaxTotalConnections() + "\n");
    // sb.append("Max outgoing links to follow: " + getMaxOutgoingLinksToFollow() +
    // "\n");
    // sb.append("Max download size: " + getMaxDownloadSize() + "\n");
    // sb.append("Should follow redirects?: " + isFollowRedirects() + "\n");
    // sb.append("Proxy host: " + getProxyHost() + "\n");
    // sb.append("Proxy port: " + getProxyPort() + "\n");
    // sb.append("Proxy username: " + getProxyUsername() + "\n");
    // sb.append("Thread monitoring delay: " + getThreadMonitoringDelaySeconds() +
    // "\n");
    // sb.append("Thread shutdown delay: " + getThreadShutdownDelaySeconds() +
    // "\n");
    // sb.append("Cleanup delay: " + getCleanupDelaySeconds() + "\n");
    // sb.append("Cookie policy: " + getCookiePolicy() + "\n");
    // sb.append("Respect nofollow: " + isRespectNoFollow() + "\n");
    // sb.append("Respect noindex: " + isRespectNoIndex() + "\n");
    // sb.append("Halt on error: " + isHaltOnError() + "\n");
    // sb.append("Allow single level domain:" + isAllowSingleLevelDomain() + "\n");
    // sb.append("Batch read size: " + getBatchReadSize() + "\n");
    // return sb.toString();
    // }
}
