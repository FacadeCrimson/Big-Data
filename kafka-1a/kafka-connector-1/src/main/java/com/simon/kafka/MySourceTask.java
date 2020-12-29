package com.simon.kafka;

import com.simon.kafka.model.CurrentWeather;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import static com.simon.kafka.CurrentWeatherSchemas.*;

public class MySourceTask extends SourceTask {
  private static final Logger log = LoggerFactory.getLogger(MySourceTask.class);
  public MySourceConnectorConfig config;

  protected Instant nextQuerySince;
  protected Integer lastIssueNumber;
  protected Integer nextPageToVisit = 1;
  protected Instant lastUpdatedAt;

  APIHttpClient apihttpclient;

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> map) {
    config = new MySourceConnectorConfig(map);
    initializeLastVariables();
    apihttpclient = new APIHttpClient(config);
  }

  private void initializeLastVariables(){
    Map<String, Object> lastSourceOffset = null;
    lastSourceOffset = context.offsetStorageReader().offset(sourcePartition());
    if( lastSourceOffset == null){
        // we haven't fetched anything yet, so we initialize to 7 days ago
        nextQuerySince = config.getSince();
        lastIssueNumber = -1;
    } else {
        Object updatedAt = lastSourceOffset.get(UPDATED_AT_FIELD);
        Object issueNumber = lastSourceOffset.get(NUMBER_FIELD);
        Object nextPage = lastSourceOffset.get(NEXT_PAGE_FIELD);
        if(updatedAt != null && (updatedAt instanceof String)){
            nextQuerySince = Instant.parse((String) updatedAt);
        }
        if(issueNumber != null && (issueNumber instanceof String)){
            lastIssueNumber = Integer.valueOf((String) issueNumber);
        }
        if (nextPage != null && (nextPage instanceof String)){
            nextPageToVisit = Integer.valueOf((String) nextPage);
        }
    }
}

  @Override
  public SourceRecord poll() throws InterruptedException {
      apihttpclient.sleepIfNeed();

      // fetch data
      JSONObject jsonobject = apihttpclient.getCurrentWeather();
      // we'll count how many results we get with i
      CurrentWeather currentweather = CurrentWeather.fromJson((JSONObject) jsonobject);
      SourceRecord sourceRecord = generateSourceRecord(currentweather);
      apihttpclient.sleep();
      return sourceRecord;
  }

  private SourceRecord generateSourceRecord(CurrentWeather currentweather) {
      return new SourceRecord(
              sourcePartition(),
              sourceOffset(),
              config.getTopic(),
              null, // partition will be inferred by the framework
              KEY_SCHEMA,
              buildRecordKey(currentweather),
              VALUE_SCHEMA,
              buildRecordValue(currentweather),
              Instant.now().toEpochMilli());
  }

  @Override
  public void stop() {
      // Do whatever is required to stop your task.
  }

  private Map<String, String> sourcePartition() {
      Map<String, String> map = new HashMap<>();
      map.put(LAT_FIELD, config.getLat());
      map.put(LON_FIELD, config.getLon());
      return map;
  }

  private Map<String, String> sourceOffset() {
      Map<String, String> map = new HashMap<>();
      map.put("current time", Instant.now().toString());
      return map;
  }

  private Struct buildRecordKey(CurrentWeather currentweather){
      // Key Schema
      Struct key = new Struct(KEY_SCHEMA)
              .put(LAT_FIELD, config.getLat())
              .put(LON_FIELD, config.getLon())
              .put("current time", Instant.now().toString());
      return key;
  }

  private Struct buildRecordValue(CurrentWeather currentweather){

      // Issue top level fields
      Struct valueStruct = new Struct(VALUE_SCHEMA)
              .put(URL_FIELD, issue.getUrl())
              .put(TITLE_FIELD, issue.getTitle())
              .put(CREATED_AT_FIELD, issue.getCreatedAt().toEpochMilli())
              .put(UPDATED_AT_FIELD, issue.getUpdatedAt().toEpochMilli())
              .put(NUMBER_FIELD, issue.getNumber())
              .put(STATE_FIELD, issue.getState());

      // User is mandatory
      User user = issue.getUser();
      Struct userStruct = new Struct(USER_SCHEMA)
              .put(USER_URL_FIELD, user.getUrl())
              .put(USER_ID_FIELD, user.getId())
              .put(USER_LOGIN_FIELD, user.getLogin());
      valueStruct.put(USER_FIELD, userStruct);

      // Pull request is optional
      PullRequest pullRequest = issue.getPullRequest();
      if (pullRequest != null) {
          Struct prStruct = new Struct(PR_SCHEMA)
                  .put(PR_URL_FIELD, pullRequest.getUrl())
                  .put(PR_HTML_URL_FIELD, pullRequest.getHtmlUrl());
          valueStruct.put(PR_FIELD, prStruct);
      }

      return valueStruct;
    }
}