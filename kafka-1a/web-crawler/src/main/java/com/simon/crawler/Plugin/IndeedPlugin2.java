package com.simon.crawler.Plugin;

import java.util.List;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;

public class IndeedPlugin2 {
    public static void processHTML(KafkaProducer<String, String> producer, String url, String topic) {
        WebDriver dr = new SafariDriver();
        dr.manage().window().maximize();

        dr.get(url);
        List<WebElement> elements = dr.findElements(By.tagName("iframe"));
        for (WebElement element : elements) {
            System.out.println(element.getAttribute("id"));
        }
        dr.switchTo().frame(0);

        WebElement description = dr.findElement(By.id("jobDescriptionText"));

        producer.send(new ProducerRecord<>("description", url.split("vjk=")[1], description.getText()), new Callback() {
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
        dr.close();
    }
}
