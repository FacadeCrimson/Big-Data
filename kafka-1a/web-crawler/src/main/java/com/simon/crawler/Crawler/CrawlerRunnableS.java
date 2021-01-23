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
    private String crawl_storage;
    private ArrayList<String> prefixes;
    private CountDownLatch latch;
    private CrawlController controller;
    private ConsumerRunnable consumerRunnable;
    private String bootstrap_servers;
    private String seed_topic = "seeds";
    private String consumer_group = "seedconsumer5";

    private static final Integer NUMBER_OF_CRAWLERS = 1;
    private static final Integer DEPTH = 0;
    private static final Integer POLITENESS_DELAY = 500;
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36";

    public CrawlerRunnableS(String crawl_storage, ArrayList<String> prefixes, CountDownLatch latch,
            String bootstrap_servers) {
        this.crawl_storage = crawl_storage;
        this.prefixes = prefixes;
        this.latch = latch;
        this.bootstrap_servers = bootstrap_servers;
    }

    @Override
    public void run() {
        try {
            logger.info("Starting consumer crawlers!");
            controller = createController();
            consumerRunnable = new ConsumerRunnable(controller, bootstrap_servers, consumer_group, seed_topic);
            Thread consumerThread = new Thread(consumerRunnable);
            consumerThread.start();
            controller.addSeed("https://www.indeed.com/jobs?q=analyst&sort=date&vjk=47f5ee8b29b61c98");
            controller.start(new CrawlerFactory(prefixes), NUMBER_OF_CRAWLERS);
        } catch (Exception e) {
            logger.info("Something wrong!");
        }
    }

    @Override
    public void shutdown() {
        if (controller != null) {
            try {
                consumerRunnable.shutdown();
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
        config.setIncludeHttpsPages(true);
        config.setPolitenessDelay(POLITENESS_DELAY);
        config.setUserAgentString(USER_AGENT);
        config.setMaxDepthOfCrawling(DEPTH);
        config.setCrawlStorageFolder(crawl_storage + "/consumer/");
        config.setThreadMonitoringDelaySeconds(5);
        config.setThreadShutdownDelaySeconds(5);
        config.setCleanupDelaySeconds(5);

        PageFetcherS pageFetcher = new PageFetcherS(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        return controller;
    }

}
