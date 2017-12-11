package com.dhval.postman;

import com.dhval.Constants;
import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PublishNotificationEvent extends SOAPClient {
    private Map<String, String> queryMap;

    public PublishNotificationEvent(@Value("${client.ws.jems}") String clientURL) {
        super(clientURL);
    }

    public void build(String endPoint, String profile) {
        queryMap = queryMap(endPoint, profile);
    }

    public ResponseEntity<String> post(String filePath) throws Exception {
        return transform(queryMap, new ClassPathResource("xsl/notify.xsl").getInputStream(),
                new File(filePath));
    }

    private Map<String, String> queryMap(String url, String agency) {
        return Stream.of (
                new AbstractMap.SimpleEntry<>("Agency", agency),
                new AbstractMap.SimpleEntry<>("Target", url)
        ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
    }

}
