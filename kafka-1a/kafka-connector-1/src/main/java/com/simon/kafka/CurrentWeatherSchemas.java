package com.simon.kafka;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class CurrentWeatherSchemas {
    
    public static String COORD_FIELD = "coord";
    public static String LON_FIELD = "lon";
    public static String LAT_FIELD = "lat";

    public static String WEATHER_FIELD = "weather";
    public static String WEATHER_ID_FIELD = "id";
    public static String WEATHER_MAIN_FIELD = "main";
    public static String WEATHER_DESCRIPTION_FIELD = "description";
    public static String WEATHER_ICON_FIELD = "icon";

    public static String BASE_FIELD = "base";

    public static String MAIN_FIELD = "main";
    public static String TEMP_FIELD = "temp";
    public static String FEELS_LIKE_FIELD = "feels_like";
    public static String TEMP_MIN_FIELD = "temp_min";
    public static String TEMP_MAX_FIELD = "temp_max";
    public static String PRESSURE_FIELD = "pressure";
    public static String HUMIDITY_FIELD = "humidity";

    public static String VISIBILITY_FIELD = "visibility";

    public static String WIND_FIELD = "wind";
    public static String WIND_SPEED_FIELD = "speed";
    public static String WIND_DEG_FIELD = "deg";

    public static String CLOUDS_FIELD = "clouds";
    public static String CLOUDS_ALL_FIELD = "all";

    public static String DT_FIELD = "dt";
    
    public static String SYS_FIELD= "sys";
    public static String SYS_TYPE_FIELD = "type";
    public static String SYS_ID_FIELD = "id";
    public static String SYS_COUNTRY_FIELD = "country";
    public static String SYS_SUNRISE_FIELD = "sunrise";
    public static String SYS_SUNSET_FIELD = "sunset";

    public static String TIMEZONE_FIELD = "timezone";
    public static String ID_FIELD = "id";
    public static String NAME_FIELD = "name";
    public static String COD_FIELD = "cod";

    public static String SCHEMA_KEY = "key";
    public static String SCHEMA_VALUE = "value";
    public static String SCHEMA_VALUE_COORD = "coord";
    public static String SCHEMA_VALUE_WEATHER = "weather";
    public static String SCHEMA_VALUE_MAIN = "main";
    public static String SCHEMA_VALUE_WIND = "wind";
    public static String SCHEMA_VALUE_CLOUDS = "clouds";
    public static String SCHEMA_VALUE_SYS= "sys";

    public static Schema KEY_SCHEMA = SchemaBuilder.struct().name(SCHEMA_KEY)
            .version(1)
            .field(LON_FIELD, Schema.INT32_SCHEMA)
            .field(LAT_FIELD, Schema.INT32_SCHEMA)
            .build();
    
    public static Schema COORD_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_COORD)
            .version(1)
            .field(LON_FIELD, Schema.INT32_SCHEMA)
            .field(LAT_FIELD, Schema.INT32_SCHEMA)
            .build();

    public static Schema WEATHER_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_WEATHER)
            .version(1)
            .field(WEATHER_ID_FIELD, Schema.INT32_SCHEMA)
            .field(WEATHER_MAIN_FIELD, Schema.STRING_SCHEMA)
            .field(WEATHER_DESCRIPTION_FIELD, Schema.STRING_SCHEMA)
            .field(WEATHER_ICON_FIELD, Schema.STRING_SCHEMA)
            .build();

    public static Schema WEATHER_ARRAY_SCHEMA = SchemaBuilder.array(WEATHER_SCHEMA);
    
     public static Schema MAIN_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_MAIN)
            .version(1)
            .field(TEMP_FIELD, Schema.INT32_SCHEMA)
            .field(FEELS_LIKE_FIELD, Schema.INT32_SCHEMA)
            .field(TEMP_MIN_FIELD, Schema.INT32_SCHEMA)
            .field(TEMP_MAX_FIELD, Schema.INT32_SCHEMA)
            .field(PRESSURE_FIELD, Schema.INT32_SCHEMA)
            .field(HUMIDITY_FIELD, Schema.INT32_SCHEMA)
            .build();
    
    public static Schema WIND_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_WIND)
            .version(1)
            .field(WIND_SPEED_FIELD, Schema.INT32_SCHEMA)
            .field(WIND_DEG_FIELD, Schema.INT32_SCHEMA)
            .build();

    public static Schema CLOUDS_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_CLOUDS)
            .version(1)
            .field(CLOUDS_ALL_FIELD, Schema.INT32_SCHEMA)
            .build();

    public static Schema SYS_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_SYS)
            .version(1)
            .field(SYS_TYPE_FIELD, Schema.INT32_SCHEMA)
            .field(SYS_ID_FIELD, Schema.INT32_SCHEMA)
            .field(SYS_COUNTRY_FIELD, Schema.STRING_SCHEMA)
            .field(SYS_SUNRISE_FIELD, Schema.INT32_SCHEMA)
            .field(SYS_SUNSET_FIELD, Schema.INT32_SCHEMA)
            .build();

    public static Schema VALUE_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE)
            .version(1)
            .field(COORD_FIELD,COORD_SCHEMA)
            .field(WEATHER_FIELD,WEATHER_ARRAY_SCHEMA)
            .field(BASE_FIELD, Schema.STRING_SCHEMA)
            .field(MAIN_FIELD, MAIN_SCHEMA)
            .field(VISIBILITY_FIELD, Schema.STRING_SCHEMA)
            .field(WIND_FIELD,WIND_SCHEMA)
            .field(CLOUDS_FIELD,CLOUDS_SCHEMA)
            .field(DT_FIELD,Schema.INT32_SCHEMA)
            .field(SYS_FIELD, SYS_SCHEMA) 
            .field(TIMEZONE_FIELD,Schema.INT32_SCHEMA)
            .field(ID_FIELD,Schema.INT32_SCHEMA)
            .field(NAME_FIELD, Schema.STRING_SCHEMA)
            .field(COD_FIELD,Schema.INT32_SCHEMA)
            .build();
}
