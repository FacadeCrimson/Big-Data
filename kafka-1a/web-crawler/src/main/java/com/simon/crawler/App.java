package com.simon.crawler;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

import com.simon.crawler.Crawler.CrawlerRunnable;
import com.simon.crawler.Crawler.CrawlerRunnableS;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class.getName());
    private final AppConfig config;
    private CountDownLatch latch;
    public KafkaProducer<String, String> producer;
    public static final ArrayBlockingQueue<String> htmls = new ArrayBlockingQueue<>(50);

    public App(AppConfig config) {
        try {
            config.validate();
        } catch (Exception e) {
            logger.error("Failed to start app.", e);
        }
        this.config = config;
        this.producer = createProducer(config.getBootstrapServers());
        this.latch = new CountDownLatch(3);
    }

    public KafkaProducer<String, String> createProducer(String bootstrap_servers) {
        Properties properties = new Properties();

        // kafka bootstrap server
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // producer acks
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        return producer;
    }

    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        config.setTopic("test");
        config.addSeeds(System.getenv("testseed"));
        config.addPrefixes(System.getenv("prefix"));
        App app = new App(config);

        logger.info("Creating producer thread.");
        ProducerRunnable producerRunnable = new ProducerRunnable(app.producer, htmls, app.config.getTopic(), app.latch);
        Thread producerThread = new Thread(producerRunnable);
        producerThread.start();

        // logger.info("Creating periodic crawler thread.");
        // CrawlerRunnable crawlerPeriodicRunnable = new CrawlerRunnable(config.getCrawlStorage(), config.getSeeds(),
        //         config.getPrefixes(), app.latch);
        // Thread crawlPeriodicThread = new Thread(crawlerPeriodicRunnable);
        // crawlPeriodicThread.start();

        logger.info("Creating consumer crawler threads.");
        CrawlerRunnableS crawlerRunnable = new CrawlerRunnableS(config.getCrawlStorage(),
                config.getPrefixes(), app.latch, config.getBootstrapServers());
        Thread crawlThread = new Thread(crawlerRunnable);
        crawlThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Caught shutdown hook.");
            try {
                // crawlerPeriodicRunnable.shutdown();
                crawlerRunnable.shutdown();
                producerRunnable.shutdown();
                app.producer.close();
                app.latch.await();
                logger.info("All threads have been shut down.");
            } catch (Exception e) {
                logger.error("Error.", e);
            } finally {
                logger.info("Application has exited.");
            }
        }));

        try {
            app.latch.await();
        } catch (InterruptedException e) {
            logger.error("Application got interrupted", e);
        } finally {
            logger.info("Application is closing");
        }
    }
}
