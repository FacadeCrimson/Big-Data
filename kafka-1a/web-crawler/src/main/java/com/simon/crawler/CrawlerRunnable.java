package com.simon.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerRunnable implements RunnableS{
    private volatile boolean running = true;
    private Integer cycleNum;
    private static final Integer NUMBER_OF_CRAWLERS = 1;
    private static final Integer DEPTH = 1;
    private static final Integer POLITENESS_DELAY = 1000;
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36";

    private Logger logger = LoggerFactory.getLogger(CrawlerRunnable.class.getName());
    private CrawlController controller;
    
    public CrawlerRunnable(Integer cycleNum){
        this.cycleNum = cycleNum;
    }
    
    @Override
    public void run(){
        while(running){
            try{
                logger.info("Starting crawlers!");
                controller = this.createController();
                controller.start(Crawler.class, NUMBER_OF_CRAWLERS);
                Thread.sleep(6000);
            }catch(Exception e){
                logger.info("Something wrong!");
            }
        }
    }
    @Override
    public void shutdown(){
        if(controller != null){
            try{
                controller.shutdown();
            }catch(Exception e){
                logger.info("Error.",e);
            }finally{
                logger.info("CrawlerThread "+Thread.currentThread().getName()+" shut down.");
            }
        }
        Thread.currentThread().interrupt();
        return;
    }

    private CrawlController createController() throws Exception{
        CrawlConfig config = new CrawlConfig();
        String CRAWL_STORAGE = System.getenv("storage")+"/"+cycleNum.toString()+"/";
        config.setIncludeHttpsPages(true);
        config.setPolitenessDelay(POLITENESS_DELAY);
        config.setCrawlStorageFolder(CRAWL_STORAGE);
        config.setUserAgentString(USER_AGENT);
        config.setMaxDepthOfCrawling(DEPTH);
        config.setShutdownOnEmptyQueue(true);
        // config.setIncludeBinaryContentInCrawling(true);
    
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
    
        controller.addSeed(System.getenv("testseed"));

        return controller;
    }
    
}
