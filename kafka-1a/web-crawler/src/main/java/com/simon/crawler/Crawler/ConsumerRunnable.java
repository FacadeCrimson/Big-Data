package com.simon.crawler.Crawler;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import com.simon.crawler.RunnableS;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlController;

public class ConsumerRunnable implements RunnableS {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class.getName());
    private volatile boolean running = true;
    private CrawlController controller;
    private KafkaConsumer<String, String> consumer;

    public ConsumerRunnable(CrawlController controller, String bootstrap_servers, String groupId, String topic) {
        this.controller = controller;
        logger.info("Creating consumer.");
        this.consumer = createConsumer(bootstrap_servers, groupId, topic);
    }

    @Override
    public void run() {
        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
                logger.info("Received "+records.count()+" records");

                for (ConsumerRecord<String, String> record : records) {
                    logger.info("Key: " + record.key() + ", Value: " + record.value());
                    controller.addSeed(record.value());
                }

            }
        } catch (WakeupException e) {
            logger.info("Received shutdown signal!");
        } finally {
            consumer.close();
        }
    }

    @Override
    public void shutdown() {
        running = false;
        consumer.wakeup();
    }

    public KafkaConsumer<String, String> createConsumer(String bootstrap_servers, String groupId, String topic) {
        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }

}
