package com.simon.crawler.Crawler;

import java.util.ArrayList;

import com.simon.crawler.RunnableS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerRunnable implements RunnableS {
    private volatile boolean running = true;
    private Integer cycle_num;
    private String crawl_storage;
    private ArrayList<String> seeds;
    private ArrayList<String> prefixes;

    private static final Integer NUMBER_OF_CRAWLERS = 1;
    private static final Integer DEPTH = 1;
    private static final Integer POLITENESS_DELAY = 1000;
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36";

    private Logger logger = LoggerFactory.getLogger(CrawlerRunnable.class.getName());
    private ArrayList<CrawlController> controller_list;

    public CrawlerRunnable(Integer cycle_num, String crawl_storage, ArrayList<String> seeds,
            ArrayList<String> prefixes) {
        this.cycle_num = cycle_num;
        this.crawl_storage = crawl_storage;
        this.seeds = seeds;
        this.prefixes = prefixes;
    }

    @Override
    public void run() {
        while (running) {
            try {
                logger.info("Starting crawlers!");
                CrawlController controller = createController();
                controller.start(new CrawlerFactory(prefixes), NUMBER_OF_CRAWLERS);;
                controller_list.add(createController());
                Thread.sleep(6000);
            } catch (Exception e) {
                logger.info("Something wrong!");
            }
        }
    }

    @Override
    public void shutdown() {
        if (controller_list != null) {
            try {
                for (CrawlController controller : controller_list) {
                    controller.shutdown();
                }
                running=false;
            } catch (Exception e) {
                logger.info("Error.", e);
            } finally {
                logger.info("CrawlerThread " + Thread.currentThread().getName() + " shut down.");
            }
        }
        Thread.currentThread().interrupt();
        return;
    }

    private CrawlController createController() throws Exception {
        CrawlConfig config = new CrawlConfig();
        String CRAWL_STORAGE = crawl_storage + "/" + cycle_num.toString() + "/";
        config.setIncludeHttpsPages(true);
        config.setPolitenessDelay(POLITENESS_DELAY);
        config.setCrawlStorageFolder(CRAWL_STORAGE);
        config.setUserAgentString(USER_AGENT);
        config.setMaxDepthOfCrawling(DEPTH);
        config.setThreadMonitoringDelaySeconds(3);
        // List<Header> headers = Arrays.asList(
        // new BasicHeader("Accept", "text/html,text/xml"),
        // new BasicHeader("Accept-Language", "en-gb, en-us, en-uk")
        // );
        // config.setDefaultHeaders(headers);
        // config.setIncludeBinaryContentInCrawling(true);

        PageFetcherS pageFetcher = new PageFetcherS(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        System.out.println(seeds.toString());
        for (String seed : seeds) {
                controller.addSeed(seed);
        }
        return controller;
    }

}
