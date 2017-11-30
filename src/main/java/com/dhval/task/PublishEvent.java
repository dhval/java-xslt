package com.dhval.task;

import com.dhval.Application;
import com.dhval.postman.PublishNotificationEvent;
import com.dhval.postman.SOAPClient;
import com.dhval.utils.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("PublishEvent")
public class PublishEvent extends Task {
    private static final Logger LOG = LoggerFactory.getLogger(PublishEvent.class);

    @Value("${client.ws.jems}")
    String clientURL;

    @Value("${data.config:config/jems-dhs-config.json}")
    String configJson;

    private String endPoint;
    private String profile;
    private List<String> files;


    @PostConstruct
    private void configure() {
        try {
            if (!FileUtils.isFilePresent(configJson)) {
                return;
            }
            init(configJson);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public Task init(String config) throws Exception {
        Map readValue = new ObjectMapper().readValue(new File(config), Map.class);
        Map data = (Map) readValue.get("data");
        endPoint = (String) data.get("endpoint");
        profile = (String) data.get("profile");
        files = (List<String>) data.get("files");
        LOG.info("D!" + data.get("name"));
        return this;
    }

    public void run() throws Exception {
        final Map<String, String> queryMap = queryMap(endPoint, profile);
        SOAPClient client = new SOAPClient(clientURL) {
            public ResponseEntity<String> post(String filePath) throws Exception {
                return transform(queryMap, new ClassPathResource("xsl/notify.xsl").getFile(),
                        new File(filePath));
            }
        };
        for (String filePath : files) {
            LOG.info("File- " + filePath);
            ResponseEntity<String> response = client.post(filePath);
            LOG.info("Serialized result: " + response.toString());
        }
    }

    private Map<String, String> queryMap(String url, String agency) {
        return Stream.of(
                new AbstractMap.SimpleEntry<>("Agency", agency),
                new AbstractMap.SimpleEntry<>("Target", url)
        ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
    }

}
