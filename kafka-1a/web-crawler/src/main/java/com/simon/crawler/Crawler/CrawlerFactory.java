package com.simon.crawler.Crawler;

import java.util.ArrayList;

import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory;

public class CrawlerFactory implements WebCrawlerFactory<Crawler> {

    final Crawler crawler;

    public CrawlerFactory(ArrayList<String> prefixes) {
        this.crawler = new Crawler(prefixes);
    }

    @Override
    public Crawler newInstance() throws Exception {
        return this.crawler;
    }
}