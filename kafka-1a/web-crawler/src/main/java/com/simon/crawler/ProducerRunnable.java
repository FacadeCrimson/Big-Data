package com.simon.crawler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.simon.crawler.Plugin.IndeedPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.clients.producer.KafkaProducer;

public class ProducerRunnable implements RunnableS {
    private static final Logger logger = LoggerFactory.getLogger(ProducerRunnable.class.getName());
    private volatile boolean running = true;
    private KafkaProducer<String, String> producer;
    private ArrayBlockingQueue<String> htmls;
    private String topic;
    private CountDownLatch latch;

    public ProducerRunnable(KafkaProducer<String, String> producer, ArrayBlockingQueue<String> htmls, String topic,
            CountDownLatch latch) {
        this.producer = producer;
        this.htmls = htmls;
        this.topic = topic;
        this.latch = latch;
    }

    @Override
    public void run() {
        while (running) {
            String html = null;
            try {
                html = htmls.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Interruppted.", e);
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
                latch.countDown();
            }
        }
        Thread.currentThread().interrupt();
        return;
    }
}
