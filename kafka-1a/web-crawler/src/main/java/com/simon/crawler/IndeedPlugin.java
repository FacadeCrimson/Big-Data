package com.simon.crawler;

import java.util.Properties;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IndeedPlugin {
    private static final String TOPIC = "post";

    public static void process(String html){
        Document doc= Jsoup.parse(html);
        Elements cards = doc.getElementsByClass("jobsearch-SerpJobCard unifiedRow row result");

        KafkaProducer<String, String>  producer = createProducer();

        for(Element card : cards){
            producer.send(newPost(card));
        }
        producer.close();
    }

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
    

    public static ProducerRecord<String, String> newPost(Element card) {
        ObjectNode post = JsonNodeFactory.instance.objectNode();
        post.put("id",card.attr("data-jk"));

        Element title = card.getElementsByClass("title").first().getElementsByTag("a").first();
        post.put("title",title.attr("title"));
        post.put("link",title.attr("href"));

        Element company = card.getElementsByClass("company").first();
        post.put("company",company.text());

        Element loc = card.getElementsByClass("location accessible-contrast-color-location").first();
        post.put("location",loc.text());

        Element summary = card.getElementsByClass("summary").first();
        post.put("summary",summary.text());

        Element date = card.getElementsByClass("date").first();
        post.put("date",date.text());

        return new ProducerRecord<>(TOPIC, card.attr("data-jk"), post.toString());
    }
}
