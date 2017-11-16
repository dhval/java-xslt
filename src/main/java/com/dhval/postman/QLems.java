package com.dhval.postman;

import com.dhval.Constants;
import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Map;

@Service
public class QLems extends SOAPClient {

    public QLems(@Value("${client.ws.lems}") String clientURL) {
        super(clientURL);
    }

    public ResponseEntity<String> post(Map<String, String> queryMap) throws Exception {
        return transform(queryMap, new ClassPathResource("xsl/lems.xsl").getFile(), null);
    }

}
