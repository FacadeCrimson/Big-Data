package com.simon.crawler.Plugin;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IndeedPlugin {
    public static void processHTML(KafkaProducer<String, String> producer, String html, String topic) {
        Document doc = Jsoup.parse(html);
        Elements cards = doc.getElementsByClass("jobsearch-SerpJobCard unifiedRow row result");
        for (Element card : cards) {
            producer.send(IndeedPlugin.newPost(card, topic), new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static ProducerRecord<String, String> newPost(Element card, String topic) {
        ObjectNode post = JsonNodeFactory.instance.objectNode();
        post.put("id", card.attr("data-jk"));

        Element title = card.getElementsByClass("title").first().getElementsByTag("a").first();
        post.put("title", title.attr("title"));
        post.put("link", title.attr("href"));

        Element company = card.getElementsByClass("company").first();
        post.put("company", company.text());

        Element loc = card.getElementsByClass("location accessible-contrast-color-location").first();
        post.put("location", loc.text());

        Element summary = card.getElementsByClass("summary").first();
        post.put("summary", summary.text());

        Element date = card.getElementsByClass("date").first();
        post.put("date", date.text());

        return new ProducerRecord<>(topic, card.attr("data-jk"), post.toString());
    }
}
