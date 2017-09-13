package com.dhval;

import com.dhval.utils.*;
import net.sf.saxon.s9api.*;
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

    @Test
    public void run3() throws Exception {
        String wsdlFile = "/Users/dhval/drive/OA5TPPJNET084/MMI/PAMMI_SSP_v1.0.0/SIP WS 1.1/PAMMI.wsdl";
        String xsdFile = "/Users/dhval/drive/OA5TPPJNET084/MMI/PAMMI_SSP_v1.0.0/schema/PAMMI.xsd";
        List<String> list = new FindSchemaLocations().buildFromWsdl(wsdlFile);
        List<String> xsdFiles = new FindSchemaLocations().buildFromXsd(list);
        int count = 0;
        for (String file : xsdFiles) {
            // recursively traverse new found xsd files.
            LOG.info((++count) + "# " + file);
        }
    }

    //@Test
    public void transform() throws Exception {
        String xmlFile = "/Users/dhval/drive/OA5TPPJNET084/MMI/PAMMI_SSP_v1.0.0/SIP WS 1.1/PAMMI.wsdl";
        String outFile = "/Users/dhval/drive/OA5TPPJNET084/MMI/PAMMI_SSP_v1.0.0/SIP WS 1.1/PAMMI-Full.wsdl";
        String xslFile = "src/main/resources/xsl/flatten-wsdl.xsl";
        generateSchemas();
        new TransformUtils().transform(xslFile, xmlFile, outFile);
    }

    private void generateSchemas() throws IOException {
        String path = "/Users/dhval/drive/OA5TPPJNET084/MMI/PAMMI_SSP_v1.0.0/schema";
        String[] files = FileUtils.allFilesByType(path, "xsd");
        for (String file : files) {
            LOG.info(file);
        }
        LOG.info("Found #" + files.length);
        new XMLWriter().buildSchemas(files);
    }
}
