package com.dhval;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Test
    public void run() throws Exception {
        Map readValue = new ObjectMapper().readValue(new File("config.json"), Map.class);
        LOG.info("Hello D!" + readValue.keySet());
    }
}
