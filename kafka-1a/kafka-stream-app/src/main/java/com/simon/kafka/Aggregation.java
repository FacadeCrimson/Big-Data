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
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class Aggregation {
    private static final Logger logger = LoggerFactory.getLogger(Aggregation.class.getName());
	/**
    * Constructor
    */
    private Aggregation(){}
    
    /**
     * Serde for serilization and deserialization
     */
    private static final Serializer<JsonNode> jsonsSerializer = new JsonSerializer();
    private static final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
    private static final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonsSerializer, jsonDeserializer);

    public Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, Long> posts = builder.stream("testoutput", Consumed.with(Serdes.String(), jsonSerde))
        .filterNot((key,value) -> (value.get("location").asText() == "Remote"))
        .mapValues(value->extractLocation(value))
        .selectKey((key,value) -> (value.get("county").asText()+" "+value.get("state").asText()))
        .groupByKey(Grouped.with(Serdes.String(), jsonSerde))
        .count(Materialized.as("location-count"));

        posts.toStream().to("locationcount", Produced.with(Serdes.String(), Serdes.Long()));

        return builder.build();
    }

    public JsonNode extractLocation(JsonNode value){
        ObjectNode newValue = value.deepCopy();
        String locString = newValue.get("location").asText();
        String[] splitString = locString.split(",",2);
        if(splitString.length>1){
            newValue.put("county",splitString[0]);
            newValue.put("state",splitString[1].substring(1,3));
            if(splitString[1].length()==9){
                newValue.put("code",splitString[1].substring(4));
            }else if(splitString[1].length()>9){
                newValue.put("code",splitString[1].substring(4,9));
            }else{
                newValue.put("code",(String) null);
            }
        }else{
            newValue.put("county",(String) null);
            newValue.put("state",(String) null);
            newValue.put("code",(String) null);
        }
        return newValue;
    }

    /**
     * Run app.
     */
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "aggregation5");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        Aggregation aggregation = new Aggregation();
        KafkaStreams streams = new KafkaStreams(aggregation.createTopology(), config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            logger.info("Caught shutdown hook.");
            try {
                streams.close();
                logger.info("All threads have been shut down.");
            } catch (Exception e) {
                logger.error("Error.", e);
            } finally {
                logger.info("Application has exited.");
            }
        
        }));
    }
}
