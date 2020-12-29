package com.simon.kafka.validators;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class LatLonValidator implements ConfigDef.Validator {

    @Override
    public void ensureValid(String name, Object value) {
        Integer LatLon = (Integer) value;
        if(name=="longitude"){
            if (!(-180 <= LatLon && LatLon <= 180)){
                throw new ConfigException(name, value, "Invalid coordinate.");
            }
        }
        if(name=="latitude"){
            if (!(-90 <= LatLon && LatLon <= 90)){
                throw new ConfigException(name, value, "Invalid coordinate.");
            }
        }
        
    }
}
