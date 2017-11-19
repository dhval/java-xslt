package com.dhval;

import com.dhval.postman.PublishNotificationEvent;
import com.dhval.postman.PutCCEMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class PublishNotificationEventTest {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    /**
     * "tmp/ERInmate-66ca-a9b5-5bf2-351e-15f0-127e21e-data.xml",
     "tmp/ERInmateMaintenance.xml"
     = "https://www.igxs-sat.state.pa.us/sat/EventMessageConsumerService"
     = "DHS_MEDICALASSISTANCE"
     */

    @Value("${data.endpoint}")
    String url;
    @Value("${data.profile}")
    String agency;

    @Value("#{${data.files}}")
    Map<String, String> FILES;

    //@Value("#{${propertyname}}")  private Map<String,String> propertyname;

    @Autowired
    PublishNotificationEvent post;

    private void postJson(String filePath) {

        try {
            ResponseEntity<String> response = post.post(filePath);
            LOG.info(filePath);
            LOG.info("Serialized result: " + response.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void run() throws Exception {
        post.build(url, agency);
        LOG.info(FILES.toString());
        for (String path: FILES.keySet()) {
            LOG.info("File- " + FILES.get(path));
            postJson(path);
        }
    }

}
