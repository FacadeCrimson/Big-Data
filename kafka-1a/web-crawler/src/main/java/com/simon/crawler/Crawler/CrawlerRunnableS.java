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

public class CrawlerRunnableS implements RunnableS {
    private Logger logger = LoggerFactory.getLogger(CrawlerRunnable.class.getName());
    private ArrayList<CrawlController> controller_list = new ArrayList<CrawlController>();
    private Integer crawler_num = 0;
    private String crawl_storage;
    private ArrayList<String> seeds;
    private ArrayList<String> prefixes;
    private CountDownLatch latch;
    private CrawlController controller;
    private ConsumerRunnable seedRunnable;

    private static final Integer NUMBER_OF_CRAWLERS = 1;
    private static final Integer DEPTH = 0;
    private static final Integer POLITENESS_DELAY = 500;
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36";

    public CrawlerRunnableS(String crawl_storage, ArrayList<String> seeds, ArrayList<String> prefixes,
            CountDownLatch latch) {
        this.crawl_storage = crawl_storage;
        this.seeds = seeds;
        this.prefixes = prefixes;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            logger.info("Starting crawlers!");
            controller = createController();
            controller.startNonBlocking(new CrawlerFactory(prefixes), NUMBER_OF_CRAWLERS);
            seedRunnable = new ConsumerRunnable(controller, "127.0.0.1:9092", "seedconsumer2", "seeds");
            Thread seedThread = new Thread(seedRunnable);
            seedThread.start();
        } catch (Exception e) {
            logger.info("Something wrong!");
        }
    }

    @Override
    public void shutdown() {
        if (controller_list != null) {
            try {
                seedRunnable.shutdown();
                controller.shutdown();
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
        String crawl_storage_folder = crawl_storage + "/" + crawler_num.toString() + "/";
        config.setIncludeHttpsPages(true);
        config.setPolitenessDelay(POLITENESS_DELAY);
        config.setUserAgentString(USER_AGENT);
        config.setMaxDepthOfCrawling(DEPTH);
        config.setThreadMonitoringDelaySeconds(3);
        config.setCrawlStorageFolder(crawl_storage_folder);

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
