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

    // @Override
    // public String toString() {
    // StringBuilder sb = new StringBuilder();
    // return sb.toString();
    // }
}
