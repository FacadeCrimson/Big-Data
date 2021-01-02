package com.simon.kafka.model;

import org.json.JSONObject;

import org.junit.jupiter.api.Test;

public class CurrentWeatherTest {

    String issueStr = "{\"coord\":{\"lon\":-77.04,\"lat\":38.9},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"base\":\"stations\",\"main\":{\"temp\":279.91,\"feels_like\":273.61,\"temp_min\":278.71,\"temp_max\":280.93,\"pressure\":1022,\"humidity\":52},\"visibility\":10000,\"wind\":{\"speed\":5.7,\"deg\":200},\"clouds\":{\"all\":75},\"dt\":1609172418,\"sys\":{\"type\":1,\"id\":3787,\"country\":\"US\",\"sunrise\":1609158363,\"sunset\":1609192419},\"timezone\":-18000,\"id\":4140963,\"name\":\"Washington D.C.\",\"cod\":200}";

    private JSONObject issueJson = new JSONObject(issueStr);
    @Test
    public void canParseJson(){
        System.out.println(issueJson.getJSONObject("coord").getInt("lon"));
    }
}
