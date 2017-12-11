package com.dhval.utils;

import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SaxonUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SaxonUtils.class);

    private static Processor proc = new Processor(false);
    private static XPathCompiler xpath = proc.newXPathCompiler();

    static {
        xpath.declareNamespace("jnet", "http://www.jnet.state.pa.us/niem/JNET/jnet-core/1");
    }

    public static Serializer getSerializer() throws FileNotFoundException {
        Serializer serializer = new Serializer();
        serializer.setOutputProperty(Serializer.Property.METHOD, "xml");
        serializer.setOutputProperty(Serializer.Property.INDENT, "yes");
        serializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "no");
        return serializer;
    }

    public static Serializer getSerializer(String file) throws FileNotFoundException {
        Serializer serializer = getSerializer();
        serializer.setOutputStream(new FileOutputStream(new File(file)));
        return serializer;
    }

    public static XPathSelector getXPathSelector(String pathToInputFile, String expression)
            throws SaxonApiException, FileNotFoundException {

        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode xmlDoc = builder.build(new File(pathToInputFile));
        XPathSelector selector = xpath.compile(expression).load();
        selector.setContextItem(xmlDoc);
        return selector;
    }

    public static XPathSelector getXPathSelectorFromString(String input, String expression)
            throws SaxonApiException, FileNotFoundException {
        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);

        StringReader reader = new StringReader(input);
        XdmNode xmlDoc = builder.build(new StreamSource(reader));

        XPathSelector selector = xpath.compile(expression).load();
        selector.setContextItem(xmlDoc);
        return selector;
    }

    public static void extractXpathToFile(String xpath, String pathToInputFile, String pathToOutputFile)
            throws SaxonApiException, FileNotFoundException {
        XQueryCompiler comp = proc.newXQueryCompiler();
        XQueryExecutable exp = comp.compile(xpath);
        XQueryEvaluator qe = exp.load();

        File inputFile = new File(pathToInputFile);
        SAXSource source = new SAXSource(new InputSource(new FileInputStream(inputFile)));
        source.setSystemId(inputFile.toURI().toString());

        qe.setSource(source);
        qe.run(SaxonUtils.getSerializer(pathToOutputFile));

    }

    public static boolean evaluateXpathBoolean(String filePath, String expression) throws SaxonApiException, FileNotFoundException {
        XPathSelector selector = SaxonUtils.getXPathSelector(filePath, expression);
        XdmValue xdmValue = selector.evaluate();
        for (XdmItem item : xdmValue) {
            return item.getStringValue().equals("true");
        }
        return false;
    }

    public static boolean evaluateXpathBoolean(String filePath, String[] expressions) {
        try {
            for (String expression : expressions) {
                XdmValue xdmValue = SaxonUtils.getXPathSelector(filePath, expression).evaluate();
                String val = xdmValue.getUnderlyingValue().getStringValue();
                LOG.debug(expression + " extracted: " + val);
                boolean isVal = !(StringUtils.isEmpty(val) || val.equals("false"));
                if (isVal) return true;
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        return false;
    }

    public static String[] filesMatchingXpath(String directory, String[] expression) throws SaxonApiException, IOException {
        String[] files = FileUtils.allFilesByType(directory, "xml");
        return Arrays.stream(files).filter(file -> (evaluateXpathBoolean(file, expression))).toArray(String[]::new);
    }
}
