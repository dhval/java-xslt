package com.dhval;


import com.dhval.utils.SaxonUtils;
import com.dhval.utils.XMLDiff;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmValue;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class XMLDiffTest {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    String file1 = "/Users/dhval/Desktop/tmp1.xml";
    String file2 = "/Users/dhval/Desktop/tmp2.xml";
    String xPath = "//*[local-name()='OffenderEvent']";


    @Test
    public void run() throws Exception {
        XMLDiff xmlDiff = XMLDiff.XMLDiffBuilder(file1, file2, xPath);
        LOG.info("Possible Differences# " + xmlDiff.possibleDifferenceCount());
        xmlDiff.compare();
    }
}
