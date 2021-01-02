package com.simon.kafka.model;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.simon.kafka.CurrentWeatherSchemas.*;

import java.util.ArrayList;

public class CurrentWeather {

    public static class Coord{
        private Integer lon;
        private Integer lat;

        public Coord(JSONObject jsonObject) {
            this.lon=jsonObject.getInt(LON_FIELD);
            this.lat=jsonObject.getInt(LAT_FIELD);
        }
    }
    public static class Weather{
        private Integer id;
        private String main;
        private String description;
        private String icon;

        public Weather(JSONObject jsonObject) {
            this.id=jsonObject.getInt( WEATHER_ID_FIELD );
            this.main=jsonObject.getString(WEATHER_MAIN_FIELD);
            this.description=jsonObject.getString(WEATHER_DESCRIPTION_FIELD);
            this.icon=jsonObject.getString(WEATHER_ICON_FIELD);
        }

        public Integer getId(){return this.id;}
        public String getMain(){return this.main;}
        public String getDescription(){return this.description;}
        public String getIcon(){return this.icon;}
    }
    private static class Main{
        private Integer temp;
        private Integer feels_like;
        private Integer temp_min;
        private Integer temp_max;
        private Integer pressure;
        private Integer humidity;

        public Main(JSONObject jsonObject) {
            this.temp=jsonObject.getInt(TEMP_FIELD);
            this.feels_like=jsonObject.getInt(FEELS_LIKE_FIELD);
            this.temp_min=jsonObject.getInt(TEMP_MIN_FIELD);
            this.temp_max=jsonObject.getInt(TEMP_MAX_FIELD);
            this.pressure=jsonObject.getInt(PRESSURE_FIELD);
            this.humidity=jsonObject.getInt(HUMIDITY_FIELD);
        }
    }
    private static class Wind{
        private Integer speed;
        private Integer deg;

        public Wind(JSONObject jsonObject) {
            this.speed=jsonObject.getInt(WIND_SPEED_FIELD);
            this.deg=jsonObject.getInt(WIND_DEG_FIELD);
        }
    }
    private static class Clouds{
        private Integer all;
        
        public Clouds(JSONObject jsonObject) {
            this.all=jsonObject.getInt(CLOUDS_ALL_FIELD);
        }
    }
    private static class Sys{
        private Integer type;
        private Integer id;
        private String country;
        private Integer sunrise;
        private Integer sunset;

        public Sys(JSONObject jsonObject) {
            this.type=jsonObject.getInt(SYS_TYPE_FIELD);
            this.id=jsonObject.getInt(SYS_ID_FIELD);
            this.country=jsonObject.getString(SYS_COUNTRY_FIELD);
            this.sunrise=jsonObject.getInt(SYS_SUNRISE_FIELD);
            this.sunset=jsonObject.getInt(SYS_SUNSET_FIELD);
        }
    }
    private Coord coord;
    public ArrayList<Weather> weathers = new ArrayList<Weather>();
    private String base;
    private Main main;
    private Integer visibility;
    private Wind wind;
    private Clouds clouds;
    private Integer dt;
    private Sys sys;
    private Integer timezone;
    private Integer id;
    private String name;
    private Integer cod;

    public CurrentWeather() {
    }

    // public CurrentWeather() {
    //     CurrentWeather curentweather = new CurrentWeather();
    // }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public CurrentWeather withBase(String base) {
        this.base = base;
        return this;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public CurrentWeather withVisibility(Integer visibility) {
        this.visibility = visibility;
        return this;
    }

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public CurrentWeather withDt(Integer dt) {
        this.dt = dt;
        return this;
    }

    public Integer getTimezone() {
        return timezone;
    }

    public void setTimezone(Integer timezone) {
        this.timezone = timezone;
    }

    public CurrentWeather withTimezone(Integer timezone) {
        this.timezone = timezone;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CurrentWeather withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CurrentWeather withName(String name) {
        this.name = name;
        return this;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public CurrentWeather withCod(Integer cod) {
        this.cod = cod;
        return this;
    }

    public Integer getLon(){return this.coord.lon;}
    public Integer getLat(){return this.coord.lat;}
    public Integer getTemp(){return this.main.temp;}
    public Integer getFeelsLike(){return this.main.feels_like;}
    public Integer getTempMin(){return this.main.temp_min;}
    public Integer getTempMax(){return this.main.temp_max;}
    public Integer getPressure(){return this.main.pressure;}
    public Integer getHumidity(){return this.main.humidity;}
    public Integer getSpeed(){return this.wind.speed;}
    public Integer getDeg(){return this.wind.deg;};
    public Integer getAll(){return this.clouds.all;}
    public Integer getSysType(){return this.sys.type;}
    public Integer getSysId(){return this.sys.id;}
    public String getSysCountry(){return this.sys.country;}
    public Integer getSysSunrise(){return this.sys.sunrise;}
    public Integer getSysSunset(){return this.sys.sunset;}
    
    public static CurrentWeather fromJson(JSONObject jsonObject) {

        CurrentWeather currentweather = new CurrentWeather();
        currentweather.withBase(jsonObject.getString(BASE_FIELD));
        currentweather.withCod(jsonObject.getInt(COD_FIELD));
        currentweather.withDt(jsonObject.getInt(DT_FIELD));
        currentweather.withId(jsonObject.getInt(ID_FIELD));
        currentweather.withName(jsonObject.getString(NAME_FIELD));
        currentweather.withTimezone(jsonObject.getInt(TIMEZONE_FIELD));
        currentweather.withVisibility(jsonObject.getInt(VISIBILITY_FIELD));

        currentweather.coord = new Coord(jsonObject.getJSONObject(COORD_FIELD));
        JSONArray newarray = jsonObject.getJSONArray(WEATHER_FIELD);
        for (int i = 0; i < newarray.length(); i++) {
            JSONObject newobject = newarray.getJSONObject(i);
            currentweather.weathers.add(new Weather(newobject));
          }
        currentweather.main = new Main(jsonObject.getJSONObject(MAIN_FIELD));
        currentweather.wind = new Wind(jsonObject.getJSONObject(WIND_FIELD));
        currentweather.clouds = new Clouds(jsonObject.getJSONObject(CLOUDS_FIELD));
        currentweather.sys = new Sys(jsonObject.getJSONObject(SYS_FIELD));

        return currentweather;
    }
}

