package com.dhval;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Map;

//@Configuration
//@PropertySource(value="file:config.json", factory=Config.JsonLoader.class)
public class Config {

    public static class JsonLoader implements PropertySourceFactory {

        @Override
        public org.springframework.core.env.PropertySource<?> createPropertySource(String name,
                                                                                   EncodedResource resource) throws IOException {
            Map readValue = new ObjectMapper().readValue(resource.getInputStream(), Map.class);
            return new MapPropertySource("da", readValue);
        }

    }



}
