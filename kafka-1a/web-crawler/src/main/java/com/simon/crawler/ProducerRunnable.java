package com.simon.crawler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.simon.crawler.Plugin.IndeedPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.clients.producer.KafkaProducer;

public class ProducerRunnable implements RunnableS {
    private static final Logger logger = LoggerFactory.getLogger(ProducerRunnable.class.getName());
    private volatile boolean running = true;
    private String topic;
    private ArrayBlockingQueue<String> htmls;
    private KafkaProducer<String, String> producer;

    public ProducerRunnable(KafkaProducer<String, String> producer, ArrayBlockingQueue<String> htmls, String topic) {
        this.producer = producer;
        this.htmls = htmls;
        this.topic = topic;
    }

    @Override
    public void run() {
        while (running) {
            String html = null;
            try {
                html = htmls.poll(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Getting html string failed.", e);
            }
            if (html != null) {
                IndeedPlugin.processHTML(producer, html, topic);
            }
        }
    }

    @Override
    public void shutdown() {
        if (producer != null) {
            try {
                running = false;
            } catch (Exception e) {
                logger.error("Error.", e);
            } finally {
                logger.info("ProducerThread shut down.");
            }
        }
        Thread.currentThread().interrupt();
        return;
    }
}
