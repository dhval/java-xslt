package com.dhval;

import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.apache.xerces.dom.DocumentImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class TransformTest {
    private static final Logger LOG = LoggerFactory.getLogger(TransformTest.class);

    private static final String XSL_FILE = "xsl/test.xsl";
    private static final String XML_INPUT = "sample/MJ-51301-CR-0000195-2017_CourtCaseEvent_93363352-data.xml";

    Processor proc = new Processor(false);
    XsltCompiler comp = proc.newXsltCompiler();
    String[] files =new String[] {"A", "ZB", "g", "k"};

    @Test
    public void run()  throws SaxonApiException, IOException {
        XsltExecutable exp = comp.compile(new StreamSource(new ClassPathResource(XSL_FILE).getFile()));
        XdmNode inputNode = buildNode();
        XdmNode source = proc.newDocumentBuilder().build(new StreamSource(XML_INPUT));

        Serializer out = SaxonUtils.getSerializer();
        StringWriter sw = new StringWriter();
        out.setOutputWriter(sw);

        XsltTransformer trans = exp.load();
        trans.setParameter(new QName("value"), inputNode);
        trans.setInitialContextNode(source);
        trans.setDestination(out);
        trans.transform();

        LOG.info(sw.toString());
    }

    private XdmNode buildNode() {
        //XdmNode data = proc.newDocumentBuilder().build(new StreamSource("data.xml"));
        Document xmlDoc = new DocumentImpl();

        Node item;
        Element root = xmlDoc.createElement("schemas");
        for (String file : files) {
            item = xmlDoc.createElement("file");
            item.appendChild(xmlDoc.createTextNode(file));
            root.appendChild(item);
        }
        xmlDoc.appendChild(root);
        return  proc.newDocumentBuilder().wrap(xmlDoc);
    }
}
