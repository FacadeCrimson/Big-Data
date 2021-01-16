package com.simon.crawler;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class.getName());
    private static final Integer NUM_THREADS = 1;
    private static final String TOPIC= "test";
    private static KafkaProducer<String, String> producer;
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
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        logger.info("Creating new threads.");
        ArrayList<RunnableS> runnables = new ArrayList<RunnableS>(2*NUM_THREADS);
        for (int i=0; i<NUM_THREADS; i++) 
        {   
            ProducerRunnable producerRunnable = new ProducerRunnable(TOPIC,htmls,producer);
            Thread producerThread = new Thread(producerRunnable);
            CrawlerRunnable crawlerRunnable = new CrawlerRunnable(latch);
            Thread crawlerThread = new Thread(crawlerRunnable);
            runnables.add(producerRunnable);
            runnables.add(crawlerRunnable);
            producerThread.start();
            crawlerThread.start(); 
        } 

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Caught shutdown hook.");
            try{
                for(RunnableS runnable : runnables){
                    runnable.shutdown();
                }
                logger.info("All threads have been shut down.");
            }catch(Exception e){
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
