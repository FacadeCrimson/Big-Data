package com.simon.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;


public class Deduplication {

	/**
    * Constructor
    */
    private Deduplication(){}

	/**
    * Serde for serilization and deserialization
    */
    private static final Serializer<JsonNode> jsonsSerializer = new JsonSerializer();
	private static final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
	private static final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonsSerializer, jsonDeserializer);

    public Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, JsonNode> posts = builder.table("test", Consumed.with(Serdes.String(), jsonSerde));
        
        posts.toStream().to("testoutput", Produced.with(Serdes.String(), jsonSerde));

        return builder.build();
    }

    /**
    * Run app.
    */
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "deduplication");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Exactly once processing!!
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        Deduplication deduplication =new Deduplication();

        KafkaStreams streams = new KafkaStreams(deduplication.createTopology(), config);
        streams.start();

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    
}
