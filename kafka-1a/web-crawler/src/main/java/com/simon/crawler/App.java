package com.simon.crawler;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class.getName());
    private static final Integer NUM_WEBSITES = 1;
    private static final String TOPIC= "test";
    public static KafkaProducer<String, String> producer;
    public static final ArrayBlockingQueue<String> htmls = new ArrayBlockingQueue<>(10);

    public static KafkaProducer<String, String>  createProducer(){
        Properties properties = new Properties();

        // kafka bootstrap server
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // producer acks
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all"); // strongest producing guarantee
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
        // leverage idempotent producer from Kafka 0.11 !
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // ensure we don't push duplicates

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        return producer;
    }
    public static void main(String[] args) 
    {   
        producer = createProducer();
        logger.info("Creating new threads.");
        ProducerRunnable producerRunnable = new ProducerRunnable(TOPIC,htmls,producer);
        Thread producerThread = new Thread(producerRunnable);
        producerThread.start();

        // Creating thread pool for repeating crawlers.
        ExecutorService executor = Executors.newCachedThreadPool();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Caught shutdown hook.");
            try{
                executor.shutdown();
                producerRunnable.shutdown();
                producer.close();
                logger.info("All threads have been shut down.");
            }catch(Exception e){
                logger.error("Error.",e);
            }finally{
                logger.info("Application has exited.");
            }
        }
        ));

        Integer cycleNum = 0;
        while(true){
            for (int i=0; i<NUM_WEBSITES; i++) {   
            CrawlerRunnable crawlerRunnable = new CrawlerRunnable(cycleNum);
            executor.execute(crawlerRunnable);
            }
            try{
                logger.info("Crawling Cycle "+cycleNum.toString());
                Thread.sleep(30000);
            }catch(InterruptedException e){
                logger.error("Error.",e);
            }finally{
                cycleNum+=1;
            }
        }
    }
}
