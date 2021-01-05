package com.simon.kafka;

import com.simon.kafka.model.CurrentWeather;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import static com.simon.kafka.CurrentWeatherSchemas.*;

public class MySourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(MySourceTask.class);
    public MySourceConnectorConfig config;
    APIHttpClient apihttpclient;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        config = new MySourceConnectorConfig(map);
        apihttpclient = new APIHttpClient(config);
        log.info("Task started.");
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        // apihttpclient.sleepIfNeed();
        Thread.sleep(1000);
        // fetch data
        JSONObject jsonobject = apihttpclient.getCurrentWeather();
        // we'll count how many results we get with i
        CurrentWeather currentweather = CurrentWeather.fromJson((JSONObject) jsonobject);
        SourceRecord sourceRecord = generateSourceRecord(currentweather);
        // apihttpclient.sleep();
        return Arrays.asList(sourceRecord);
    }

    private SourceRecord generateSourceRecord(CurrentWeather currentweather) {
        return new SourceRecord(
                sourcePartition(),
                sourceOffset(currentweather),
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

    private Map<String, Double> sourcePartition() {
        Map<String, Double> map = new HashMap<>();
        map.put(LAT_FIELD, config.getLat());
        map.put(LON_FIELD, config.getLon());
        return map;
    }

    private Map<String, String> sourceOffset(CurrentWeather currentweather) {
        Map<String, String> map = new HashMap<>();
        map.put(DT_FIELD, currentweather.getDt().toString());
        return map;
    }

    private Struct buildRecordKey(CurrentWeather currentweather){
        // Key Schema
        Struct key = new Struct(KEY_SCHEMA)
                .put(LAT_FIELD, config.getLat())
                .put(LON_FIELD, config.getLon())
                .put(DT_FIELD, currentweather.getDt());
        return key;
    }

    private Struct buildRecordValue(CurrentWeather currentweather){

        // Issue top level fields
        Struct valueStruct = new Struct(VALUE_SCHEMA)
            .put(BASE_FIELD, currentweather.getBase())
            .put(VISIBILITY_FIELD, currentweather.getVisibility())
            .put(DT_FIELD,currentweather.getDt())
            .put(TIMEZONE_FIELD,currentweather.getTimezone())
            .put(ID_FIELD,currentweather.getId())
            .put(NAME_FIELD, currentweather.getName())
            .put(COD_FIELD,currentweather.getCod());

        Struct coordStruct = new Struct(COORD_SCHEMA)
            .put(LON_FIELD, currentweather.getLon())
            .put(LAT_FIELD, currentweather.getLat());
        
        List<Struct> weathers = new ArrayList<Struct>();
        for (int i = 0; i < currentweather.weathers.size(); i++) {
            Struct newstruct = new Struct(WEATHER_SCHEMA)
            .put(WEATHER_ID_FIELD, currentweather.weathers.get(i).getId())
            .put(WEATHER_MAIN_FIELD, currentweather.weathers.get(i).getMain())
            .put(WEATHER_DESCRIPTION_FIELD, currentweather.weathers.get(i).getDescription())
            .put(WEATHER_ICON_FIELD, currentweather.weathers.get(i).getIcon());
            weathers.add(newstruct);
        }
        
        Struct mainStruct = new Struct(MAIN_SCHEMA)
            .put(TEMP_FIELD, currentweather.getTemp())
            .put(FEELS_LIKE_FIELD, currentweather.getFeelsLike())
            .put(TEMP_MIN_FIELD, currentweather.getTempMin())
            .put(TEMP_MAX_FIELD, currentweather.getTempMax())
            .put(PRESSURE_FIELD, currentweather.getPressure())
            .put(HUMIDITY_FIELD, currentweather.getHumidity());
        
        Struct windStruct = new Struct(WIND_SCHEMA)
            .put(WIND_SPEED_FIELD, currentweather.getSpeed())
            .put(WIND_DEG_FIELD, currentweather.getDeg());
        
        Struct cloudsStruct = new Struct(CLOUDS_SCHEMA)
            .put(CLOUDS_ALL_FIELD, currentweather.getAll());

        Struct sysStruct = new Struct(SYS_SCHEMA)
            .put(SYS_TYPE_FIELD, currentweather.getSysType())
            .put(SYS_ID_FIELD, currentweather.getSysId())
            .put(SYS_COUNTRY_FIELD, currentweather.getSysCountry())
            .put(SYS_SUNRISE_FIELD, currentweather.getSysSunrise())
            .put(SYS_SUNSET_FIELD, currentweather.getSysSunset());

        valueStruct.put(COORD_FIELD, coordStruct);
        valueStruct.put(WEATHER_FIELD, weathers);
        valueStruct.put(MAIN_FIELD, mainStruct);
        valueStruct.put(WIND_FIELD, windStruct);
        valueStruct.put(CLOUDS_FIELD, cloudsStruct);
        valueStruct.put(SYS_FIELD, sysStruct);

        return valueStruct;
        }
}