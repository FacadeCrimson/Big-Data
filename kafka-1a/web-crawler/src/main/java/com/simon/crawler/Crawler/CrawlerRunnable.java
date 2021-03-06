package com.simon.crawler.Crawler;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.simon.crawler.RunnableS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerRunnable implements RunnableS {
    private Logger logger = LoggerFactory.getLogger(CrawlerRunnable.class.getName());
    private ArrayList<CrawlController> controller_list;
    private volatile boolean running = true;
    private ArrayList<String> seeds;
    private CountDownLatch latch;
    private CrawlerFactory crawlerFactory;
    private String crawl_storage;
    private Integer crawler_num = 0;

    private static final Integer NUMBER_OF_CRAWLERS = 1;
    private static final Integer DEPTH = 0;
    private static final Integer POLITENESS_DELAY = 500;
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36";

    public CrawlerRunnable(String crawl_storage, ArrayList<String> seeds, ArrayList<String> prefixes,
            CountDownLatch latch) {
        this.crawl_storage = crawl_storage;
        this.seeds = seeds;
        this.latch = latch;
        this.controller_list = new ArrayList<CrawlController>();
        this.crawlerFactory = new CrawlerFactory(prefixes);
    }

    @Override
    public void run() {
        while (running) {
            try {
                logger.info("Starting periodic crawler!");
                CrawlController controller = createController();
                controller.startNonBlocking(crawlerFactory, NUMBER_OF_CRAWLERS);
                controller_list.add(controller);
                crawler_num += 1;
                Thread.sleep(20000);
            } catch (Exception e) {
                logger.info("Something wrong!");
            }
        }
    }

    @Override
    public void shutdown() {
        if (controller_list != null) {
            try {
                running = false;
                for (CrawlController controller : controller_list) {
                    controller.shutdown();
                }
            } catch (Exception e) {
                logger.info("Error.", e);
            } finally {
                logger.info("CrawlerThread shut down.");
                latch.countDown();
            }
        }
        Thread.currentThread().interrupt();
        return;
    }

    private CrawlController createController() throws Exception {
        CrawlConfig config = new CrawlConfig();
        config.setIncludeHttpsPages(true);
        config.setPolitenessDelay(POLITENESS_DELAY);
        config.setUserAgentString(USER_AGENT);
        config.setMaxDepthOfCrawling(DEPTH);
        config.setCrawlStorageFolder(crawl_storage + "/periodic/" + crawler_num.toString() + "/");
        config.setThreadMonitoringDelaySeconds(5);
        config.setThreadShutdownDelaySeconds(5);
        config.setCleanupDelaySeconds(5);

        PageFetcherS pageFetcher = new PageFetcherS(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        for (String seed : seeds) {
            controller.addSeed(seed);
        }
        return controller;
    }

}
