package com.dhval.config;

import com.dhval.task.Publisher;
import com.dhval.utils.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;

@Component
public class JSONConfig {
    private static final Logger LOG = LoggerFactory.getLogger(JSONConfig.class);

    @Value("${data.config:config/config.json}")
    String configJson;

    private Map json;
    private boolean enableScheduler = false;

    @PostConstruct
    private void configure() {
        try {
            json = new ObjectMapper().readValue(new File(configJson), Map.class);
            String enableSchedulerString = (String) json.get("enable-scheduler");
            enableScheduler =  enableSchedulerString.equals("yes") ? true:false;
            LOG.info("Successfully loaded json map! " + json.get("name"));
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public Map getJson() {
        return json;
    }

    public boolean isEnableScheduler() {
        return enableScheduler;
    }
}
