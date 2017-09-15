package com.dhval;

import com.dhval.sample.task.FlattenWSDL;
import com.dhval.utils.*;
import net.sf.saxon.s9api.*;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class TransformTest {
    private static final Logger LOG = LoggerFactory.getLogger(TransformTest.class);
    public static final QName XS_SCHEMA = new QName("xs", "http://www.w3.org/2001/XMLSchema", "schema");
    public static final QName XS_IMPORT = new QName("xs", "http://www.w3.org/2001/XMLSchema", "import");
    private static final String XSL_FLATTEN = "src/main/resources/xsl/flatten-wsdl.xsl";
    private static final String XSL_SORT_NS = "src/main/resources/xsl/sort-schema.xsl";
    private static final String WSDL_FILE = "/Users/dhval/drive/OA5TPPJNET084/MMI/PAMMI_SSP_v1.0.0/SIP WS 1.1/PAMMI.wsdl";
    private static final String ROOT_XSL_FILE = "/Users/dhval/drive/OA5TPPJNET084/MMI/PAMMI_SSP_v1.0.0/schema/PAMMI.xsd";
    private static final String ROOT_SCHEMA_DIR = "/Users/dhval/drive/OA5TPPJNET084/MMI/PAMMI_SSP_v1.0.0/schema";

    @Test
    public void run3() throws Exception {
        FlattenWSDL.flatten(WSDL_FILE);
    }


}
