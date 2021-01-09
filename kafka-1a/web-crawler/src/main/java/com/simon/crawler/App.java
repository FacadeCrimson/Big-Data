package com.simon.crawler;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public final class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class.getName());
    private static final Integer NUM_THREADS = 6;

    private static class CrawlerThread implements Runnable{
        private CountDownLatch latch;
        private Logger logger = LoggerFactory.getLogger(CrawlerThread.class.getName());
        private Integer i;
        
        public CrawlerThread(Integer i,CountDownLatch latch){
            this.latch = latch;
            this.i = i;
        }
        @Override
        public void run(){
            try{
                Thread.sleep(i*1000);
                logger.info("Wake up!");
            }catch(Exception e){
                logger.info("Something wrong!");
            }finally{
                latch.countDown();
            }
        }
        public void shutdown(){
            Thread.currentThread().interrupt();
            return;
        }
    }
    public static void main(String[] args) 
    {
        CountDownLatch latch = new CountDownLatch(8);
        logger.info("Creating the threads");

        ArrayList<CrawlerThread> threads = new ArrayList<CrawlerThread>(NUM_THREADS);
        for (int i=0; i<NUM_THREADS; i++) 
        {   
            CrawlerThread demo = new CrawlerThread(i,latch);
            Thread demoThread = new Thread(demo);
            threads.add(demo);
            demoThread.start(); 
        } 

        Runtime.getRuntime().addShutdownHook(new Thread(
            () -> {logger.info("Caught shutdown hook");
            for(CrawlerThread runnable : threads){
                    runnable.shutdown();
            }
            try{
                latch.await();
            }catch(InterruptedException e){
                e.printStackTrace();
            }finally{
                logger.info("Application has exited.");
            }
        }
        ));

        try{
            latch.await();
        }catch(InterruptedException e){
            logger.error("Application got interrupted.",e);
        }finally{
            logger.info("Application is closing");
        }
    }
}
