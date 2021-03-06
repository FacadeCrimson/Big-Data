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
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.redisson.Redisson;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;

public class Processing {
    private static final Logger logger = LoggerFactory.getLogger(Processing.class.getName());

    /**
     * Constructor
     */
    private Processing() {
    }

    /**
     * Serde for serilization and deserialization
     */
    private static final Serializer<JsonNode> jsonsSerializer = new JsonSerializer();
    private static final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
    private static final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonsSerializer, jsonDeserializer);

    public Topology createTopology(RSet<String> rset, String sourceTopic, String dedupicatedTopic, String seedTopic,
            String hostName) {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, JsonNode> posts = builder.stream(sourceTopic, Consumed.with(Serdes.String(), jsonSerde))
                .filter((key, value) -> (rset.add(key)));

        posts.to(dedupicatedTopic, Produced.with(Serdes.String(), jsonSerde));

        KStream<String, String> seedRecord = builder.stream(dedupicatedTopic, Consumed.with(Serdes.String(), jsonSerde))
                .mapValues((value) -> (hostName + "&vjk=" + value.get("id").asText()));

        seedRecord.to(seedTopic, Produced.with(Serdes.String(), Serdes.String()));

        return builder.build();
    }

    /**
     * Run app.
     */
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "deduplication3");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        final String sourceTopic = "test";
        final String dedupicatedTopic = "testoutput";
        final String seedTopic = "seeds";
        final String hostName = System.getenv("testseed");

        Processing deduplication = new Processing();
        RedissonClient redissonClient = deduplication.createClient();
        RSet<String> rset = redissonClient.getSet("keySet");
        KafkaStreams streams = new KafkaStreams(
                deduplication.createTopology(rset, sourceTopic, dedupicatedTopic, seedTopic, hostName), config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Caught shutdown hook.");
            try {
                streams.close();
                redissonClient.shutdown();
                logger.info("All threads have been shut down.");
            } catch (Exception e) {
                logger.error("Error.", e);
            } finally {
                logger.info("Application has exited.");
            }

        }));
    }

    public RedissonClient createClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }

}
