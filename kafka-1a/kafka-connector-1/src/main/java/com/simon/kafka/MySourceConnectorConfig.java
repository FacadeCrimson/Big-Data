package com.simon.kafka;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Importance;

import java.util.Map;

import com.simon.kafka.validators.*;

public class MySourceConnectorConfig extends AbstractConfig {

  
  public static final String TOPIC_CONFIG = "topic";
  private static final String TOPIC_DOC = "Topic to write to";

  public static final String LON_CONFIG = "longitude";
  private static final String LON_DOC = "The longitude of target place.";

  public static final String LAT_CONFIG = "latitude";
  private static final String LAT_DOC = "The latitude of target place.";

  public static final String API_KEY_CONFIG = "api.key";
  private static final String API_KEY_DOC = "Your API key for makeing the calls.";

  public MySourceConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
    super(config, parsedConfig);
  }

  public MySourceConnectorConfig(Map<String, String> parsedConfig) {
    this(conf(), parsedConfig);
  }

  public static ConfigDef conf() {
    return new ConfigDef()
            .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_DOC)
            .define(LON_CONFIG, Type.DOUBLE, -77.0506, new LatLonValidator(), Importance.HIGH, LON_DOC)
            .define(LAT_CONFIG, Type.DOUBLE, 38.8892, new LatLonValidator(), Importance.HIGH, LAT_DOC)
            .define(API_KEY_CONFIG, Type.STRING, Importance.HIGH, API_KEY_DOC);
  }

  public String getTopic() {
      return this.getString(TOPIC_CONFIG);
  }

  public Double getLat() {
    return this.getDouble(LAT_CONFIG);
  }

  public Double getLon() {
    return this.getDouble(LON_CONFIG);
  }

  public String getAPIKey() {
      return this.getString(API_KEY_CONFIG);
  }
}
