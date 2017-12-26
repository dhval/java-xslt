package com.dhval;

import com.dhval.sample.CoProc;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class CoProcTest {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    String xsltFile = "/Users/dhval/Desktop/tmp.js";
    String xmlFile = "/Users/dhval/Desktop/tmp.xml";
    String endpoint = "https://ws.jnet.beta.pa.gov/gs";

    @Test
    public void compare() throws Exception {
        CoProc.main(new String[] {xsltFile,xmlFile,endpoint});
    }
}
