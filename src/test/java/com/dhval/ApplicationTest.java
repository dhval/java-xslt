package com.dhval;

import com.dhval.sample.DPInteropClient;
import com.dhval.utils.FileUtils;
import com.dhval.utils.XMLWriter;
import com.dhval.utils.XPathList;
import net.sf.saxon.s9api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTest {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);


    @Test
    public void run3() throws Exception {
        String xsltFile = "/Users/dhval/drive/play/xslt/identity.xsl";
        String xmlFile = "/Users/dhval/drive/play/xslt/morpho-request.xml";
        String[] args = new String[] {"-x", xsltFile, "-i", xmlFile, "-h", "dpbox.foo.com", "-p", "9991"};
        DPInteropClient.main(args);
    }

    public void run() throws SaxonApiException {
        Processor proc = new Processor(false);
        DocumentBuilder builder = proc.newDocumentBuilder();
        StringReader reader = new StringReader("<a xmlns='http://a.com/' b='c'><z xmlns=''/></a>");
        XdmNode doc = builder.build(new StreamSource(reader));

        Serializer out = new Serializer();
        out.setOutputProperty(Serializer.Property.METHOD, "xml");
        out.setOutputProperty(Serializer.Property.INDENT, "yes");
        out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "no");
        out.setOutputStream(System.out);
        proc.writeXdmValue(doc, out);
    }

    public void run2() throws SaxonApiException {
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        String stylesheet =
                "<xsl:transform version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                        "  <xsl:param name='in'/>" +
                        "  <xsl:template name='main'><xsl:value-of select=\"contains($in, 'e')\"/></xsl:template>" +
                        "</xsl:transform>";
        XsltExecutable exp = comp.compile(new StreamSource(new StringReader(stylesheet)));

        Serializer out = new Serializer();
        out.setOutputProperty(Serializer.Property.METHOD, "text");
        XsltTransformer t = exp.load();
        t.setInitialTemplate(new QName("main"));

        String[] fruit = {"apple", "banana", "cherry"};
        QName paramName = new QName("in");
        for (String s: fruit) {
            StringWriter sw = new StringWriter();
            out.setOutputWriter(sw);
            t.setParameter(paramName, new XdmAtomicValue(s));
            t.setDestination(out);
            t.transform();
            System.out.println(s + ": " + sw.toString());
        }

    }

}
