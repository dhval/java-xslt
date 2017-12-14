package com.dhval;

import com.dhval.postman.SOAPClient;
import com.dhval.utils.SaxonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Collections;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class StateOffenderEventTest {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static String directory = "tmp/tmp-doc";
    private static String[] xpathExpression = {
            "//*[local-name()='OffenderEvent']/*[local-name()='EventProperties']/*[local-name()='EventName']='Transfer'"
            , "//*[local-name()='OffenderEvent']/*[local-name()='EventProperties']/*[local-name()='EventName']='ProjctdRel'"
            , "//*[local-name()='OffenderEvent']/*[local-name()='EventProperties']/*[local-name()='EventName']='Reception'"
    };
    private static String clientURL = "http://10.1.26.48:50051/jems/publish";
    private static String xsltFilePath = "xsl/StateOffenderEvent.xsl";

    @Test
    public void run() throws Exception {
        String[] files = SaxonUtils.filesMatchingXpath(directory, xpathExpression);
        for(String file: files) {
            LOG.info(file);
            ResponseEntity<String> response = null;
            SOAPClient client = new SOAPClient(clientURL) {
                public ResponseEntity<String> post(String filePath) throws Exception {
                    return transform(Collections.singletonMap("Agency", "DOC"),
                            new ClassPathResource(xsltFilePath).getInputStream(),
                            new File(filePath));
                }
            };
            try {
                response = client.post(file);
            } catch (Exception e) {
                LOG.warn("Continue on Error! ....", e);
            }
            LOG.info(response.toString());
        }
    }
}
