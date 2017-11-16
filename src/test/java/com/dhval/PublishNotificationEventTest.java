package com.dhval;

import com.dhval.postman.PublishNotificationEvent;
import com.dhval.postman.PutCCEMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class PublishNotificationEventTest {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    String url = "https://www.igxs-sat.state.pa.us/sat/EventMessageConsumerService";
    String agency = "DHS_MEDICALASSISTANCE";

    @Autowired
    PublishNotificationEvent post;

    private void postJson(Map<String, String> queryMap, String filePath) {

        try {
            ResponseEntity<String> response = post.post(queryMap, filePath);
            System.out.println(filePath);
            System.out.println("Serialized result: " + response.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void run() throws Exception {
        Map<String, String> queryMap = queryMap(url, agency);
        for (String path: FILES) {
            postJson(queryMap, path);
        }
    }

    private Map<String, String> queryMap(String url, String agency) {
        return Stream.of (
                new AbstractMap.SimpleEntry<>("Agency", agency),
                new AbstractMap.SimpleEntry<>("Target", url)
        ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
    }

    private static final String[] FILES = {
            "tmp/ERInmate-66ca-a9b5-5bf2-351e-15f0-127e21e-data.xml",
            "tmp/ERInmateMaintenance.xml"
    };

}
