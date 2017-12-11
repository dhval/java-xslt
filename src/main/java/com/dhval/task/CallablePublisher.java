package com.dhval.task;

import com.dhval.postman.SOAPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CallablePublisher implements Callable <ResponseEntity<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(CallablePublisher.class);
    private Map<String, String> queryMap;
    private String clientURL;
    private String filePath;
    private String xsltFilePath;

    public CallablePublisher(Map<String, String> queryMap, String clientURL, String filePath, String xsltFilePath) {
        this.queryMap = queryMap;
        this.clientURL = clientURL;
        this.filePath = filePath;
        this.xsltFilePath = xsltFilePath;
    }

    @Override
    public ResponseEntity<String> call() throws Exception {
        ResponseEntity<String> response = null;
        SOAPClient client = new SOAPClient(clientURL) {
            public ResponseEntity<String> post(String filePath) throws Exception {
                return transform(queryMap, new ClassPathResource(xsltFilePath).getInputStream(),
                        new File(filePath));
            }
        };
        try {
            response = client.post(filePath);
        } catch (Exception e) {
            LOG.warn("Continue on Error! ....", e);
            LOG.info("File- " + filePath);
            LOG.info("Profile: " + queryMap.get("Agency"));
            LOG.info("Endpoint: " + queryMap.get("Target"));
        }
        return response;
    }

    private Map<String, String> queryMap(String url, String agency) {
        return Stream.of(
                new AbstractMap.SimpleEntry<>("Agency", agency),
                new AbstractMap.SimpleEntry<>("Target", url)
        ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
    }
}
