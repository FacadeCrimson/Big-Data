package com.simon.kafka;

// import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.apache.kafka.connect.errors.ConnectException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import java.time.Instant;
// import java.time.LocalDateTime;
// import java.time.ZoneOffset;

public class APIHttpClient {

    private static final Logger log = LoggerFactory.getLogger(APIHttpClient.class);

    // for efficient http requests
    // private Integer XRateLimit = 9999;
    // private Integer XRateRemaining = 9999;
    // private long XRateReset = Instant.MAX.getEpochSecond();

    MySourceConnectorConfig config;

    public APIHttpClient(MySourceConnectorConfig config){
        this.config = config;
    }

    protected JSONObject getCurrentWeather() throws InterruptedException {

        HttpResponse<JsonNode> jsonResponse;
        try {
            jsonResponse = getNextIssuesAPI();

            // deal with headers in any case
            // Headers headers = jsonResponse.getHeaders();
            // XRateLimit = Integer.valueOf(headers.getFirst("X-RateLimit-Limit"));
            // XRateRemaining = Integer.valueOf(headers.getFirst("X-RateLimit-Remaining"));
            // XRateReset = Integer.valueOf(headers.getFirst("X-RateLimit-Reset"));
            switch (jsonResponse.getStatus()){
                case 200:
                    return jsonResponse.getBody().getObject();
                case 401:
                    throw new ConnectException("Bad credentials provided, please edit your config");
                case 403:
                    // long sleepTime = XRateReset - Instant.now().getEpochSecond();
                    final Integer sleepTime = 10;
                    log.info(String.format("Sleeping for %d seconds", sleepTime));
                    Thread.sleep(1000 * sleepTime);
                    return getCurrentWeather();
                default:
                    log.error(constructUrl());
                    log.error(String.valueOf(jsonResponse.getStatus()));
                    log.error(jsonResponse.getBody().toString());
                    log.error(jsonResponse.getHeaders().toString());
                    log.error("Unknown error: Sleeping 5 seconds " +
                            "before re-trying");
                    Thread.sleep(5000L);
                    return getCurrentWeather();
            }
        } catch (UnirestException e) {
            e.printStackTrace();
            Thread.sleep(5000L);
            return new JSONObject();
        }
    }

    protected HttpResponse<JsonNode> getNextIssuesAPI() throws UnirestException {
        GetRequest unirest = Unirest.get(constructUrl());
        log.debug(String.format("GET %s", unirest.getUrl()));
        return unirest.asJson();
    }

    protected String constructUrl(){
        return String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s",
                config.getLat(),
                config.getLon(),
                config.getAPIKey());
    }

    public void sleep() throws InterruptedException {
        // long sleepTime = (long) Math.ceil(
        //         (double) (XRateReset - Instant.now().getEpochSecond()) / XRateRemaining);
        final Integer sleepTime = 10;
        log.debug(String.format("Sleeping for %s seconds", sleepTime ));
        Thread.sleep(1000 * sleepTime);
    }

    public void sleepIfNeed() throws InterruptedException {
        // Sleep if needed
        // if (XRateRemaining <= 10 && XRateRemaining > 0) {
        //     log.info(String.format("Approaching limit soon, you have %s requests left", XRateRemaining));
        //     sleep();
        // }
        sleep();
    }
    
}
