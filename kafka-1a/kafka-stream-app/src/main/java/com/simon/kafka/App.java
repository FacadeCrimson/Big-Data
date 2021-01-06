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
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;

public final class App {
	/**
    * Constructor
    */
    private App(){}

	/**
    * Serde for serilization and deserialization
    */
    private static final Serializer<JsonNode> jsonsSerializer = new JsonSerializer();
	private static final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
	private static final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonsSerializer, jsonDeserializer);

    public Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> textLines = builder.stream("word-count-input");
        KTable<String, Long> wordCounts = textLines
                .mapValues(textLine -> textLine.toLowerCase())
                .flatMapValues(textLine -> Arrays.asList(textLine.split("\\W+")))
                .selectKey((key, word) -> word)
                .groupByKey()
                .count(Materialized.as("Counts"));

        /* builder.<String, String>stream("streams-plaintext-input")
                .flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+")))
                .groupBy((key, value) -> value)
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"))
                .toStream()
                .to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long())); */

        /* KStream<String, String> favColor = textLines
        .filter((key,value)->value.contains(","))
        .selectKey((key,value)->value.split(",")[0].toLowerCase())
        .mapValues(value->value.split(",")[1].toLowerCase())
        .filter((user,colour)->Arrays.asList("green","blue","red").contains(colour));

        favColor.to("user-color");

        KTable<String, String> userColor = builder.table("user-color");

        KTable<String,Long> colorCount = userColor
        .groupBy((user,colour)->new KeyValue<>(colour, colour))
        .count(); */

        wordCounts.toStream().to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));

        return builder.build();
    }

    /**
    * Run app.
    */
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Exactly once processing!!
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        App app = new App();

        KafkaStreams streams = new KafkaStreams(app.createTopology(), config);
        streams.start();

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));


        /* 
        KStream<String, JsonNode> bankTransactions = builder.stream("bank-transactions",
                Consumed.with(Serdes.String(), jsonSerde));
        
        // create the initial json object for balances
        ObjectNode initialBalance = JsonNodeFactory.instance.objectNode();
        initialBalance.put("count", 0);
        initialBalance.put("balance", 0);
        initialBalance.put("time", Instant.ofEpochMilli(0L).toString());

        KTable<String, JsonNode> bankBalance = bankTransactions
                .groupByKey(Grouped.with(Serdes.String(), jsonSerde))
                .aggregate(
                        () -> initialBalance,
                        (key, transaction, balance) -> newBalance(transaction, balance),
                        Materialized.<String, JsonNode, KeyValueStore<Bytes, byte[]>>as("bank-balance-agg")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(jsonSerde)
                );

        bankBalance.toStream().to("bank-balance-exactly-once", Produced.with(Serdes.String(), jsonSerde));
        
        private static JsonNode newBalance(JsonNode transaction, JsonNode balance) {
        // create a new balance json object
        ObjectNode newBalance = JsonNodeFactory.instance.objectNode();
        newBalance.put("count", balance.get("count").asInt() + 1);
        newBalance.put("balance", balance.get("balance").asInt() + transaction.get("amount").asInt());

        Long balanceEpoch = Instant.parse(balance.get("time").asText()).toEpochMilli();
        Long transactionEpoch = Instant.parse(transaction.get("time").asText()).toEpochMilli();
        Instant newBalanceInstant = Instant.ofEpochMilli(Math.max(balanceEpoch, transactionEpoch));
        newBalance.put("time", newBalanceInstant.toString());
        return newBalance;
        } */
    }

}
