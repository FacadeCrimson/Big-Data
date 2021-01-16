package com.simon.crawler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ProducerRunnable implements RunnableS {
    private static final Logger logger = LoggerFactory.getLogger(ProducerRunnable.class.getName());
    private volatile boolean running = true;
    private String topic;
    private ArrayBlockingQueue<String> htmls;
    private KafkaProducer<String,String> producer;
    
    public ProducerRunnable(String topic, ArrayBlockingQueue<String> htmls, KafkaProducer<String,String> producer){
        this.topic = topic;
        this.htmls = htmls;
        this.producer = producer;
    }

    @Override
    public void run(){
        while(running){
            String html = null;
            try{
                html = htmls.poll(30,TimeUnit.SECONDS);
            }catch(InterruptedException e){
                logger.error("Getting html string failed.",e);
            }
            if(html!= null){
                Document doc= Jsoup.parse(html);
                Elements cards = doc.getElementsByClass("jobsearch-SerpJobCard unifiedRow row result");
                for(Element card : cards){
                    producer.send(IndeedPlugin.newPost(card,topic), new Callback(){
                        public void onCompletion(RecordMetadata recordMetadata,Exception e){
                            if(e!=null){
                                logger.error("Sending producer record failed.",e);
                            }
                        }
                    });
                }
            }
        }
    }
    
    @Override
    public void shutdown(){
        if(producer != null){
            try{
                running = false;
            }catch(Exception e){
                logger.error("Error.",e);
            }finally{
                logger.info("ProducerThread shut down.");
            }
        }
        Thread.currentThread().interrupt();
        return;
    }
}
