package com.dhval.utils;

import net.sf.saxon.s9api.*;
import org.xml.sax.InputSource;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaxonUtils {

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
}
