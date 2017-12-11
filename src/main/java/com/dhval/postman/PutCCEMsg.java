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
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PutCCEMsg extends SOAPClient {

    public PutCCEMsg( @Value("${client.ws.mq}") String clientURL) {
        super(clientURL);
    }

    public ResponseEntity<String> post(Map<String, String> queryMap) throws Exception {
        return transform(queryMap, new ClassPathResource(Constants.XSL_FILE_CCE).getInputStream(),
                new File(Constants.XML_INPUT_CCE));
    }

    public ResponseEntity<String> post(Map<String, String> queryMap, String filePath) throws Exception {
        return transform(queryMap, new ClassPathResource(Constants.XSL_FILE_CCE).getInputStream(),
                new File(filePath));
    }

}
