package com.dhval;

import com.dhval.task.FlattenWSDL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class TransformTest {
    private static final Logger LOG = LoggerFactory.getLogger(TransformTest.class);
    private static final String WSDL_FILE = "/Users/dhval/drive/OA5TPPJNET084/MMI/PAMMI_SSP_v1.0.0/SIP WS 1.1/PAMMI.wsdl";

    @Test
    public void run() throws Exception {
        FlattenWSDL.flatten(WSDL_FILE);
    }


}
