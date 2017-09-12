package com.dhval.utils;

import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FindSchemaLocations {
    private static final Logger LOG = LoggerFactory.getLogger(FindSchemaLocations.class);
    public static final QName XS_SCHEMA = new QName("xs", "http://www.w3.org/2001/XMLSchema", "schema");
    public static final QName XS_IMPORT = new QName("xs", "http://www.w3.org/2001/XMLSchema", "import");

    public List<String> buildFromWsdl(final String wsdlFile) throws Exception {
        Path path = Paths.get(wsdlFile).getParent();
        LOG.info("Wsdl Path: " + path.toString());

        Map<String, String> values = new HashMap<>();
        List<String> files = new ArrayList<>();
        Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");

        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode booksDoc = builder.build(new File(wsdlFile));

        // find all the nodeName elements, within selector

        XPathSelector selector = xpath.compile("//wsdl:types").load();
        selector.setContextItem(booksDoc);

        for (XdmItem item : selector) {
            XdmSequenceIterator itr = ((XdmNode) item).axisIterator(Axis.CHILD, XS_SCHEMA);
            while (itr.hasNext()) {
                XdmNode schemaEl = (XdmNode) itr.next();
                XdmSequenceIterator itr2 = schemaEl.axisIterator(Axis.CHILD, XS_IMPORT);
                while (itr2.hasNext()) {
                    XdmNode importEl = (XdmNode) itr2.next();
                    String namespace = importEl.getAttributeValue(new QName("namespace"));
                    String schemaLocation = importEl.getAttributeValue(new QName("schemaLocation"));
                    // resolve to base directory
                    values.put(namespace, path.resolve(schemaLocation).normalize().toString());
                }
            }
        }


        LOG.warn("Found #" + values.size());
        Iterator<Map.Entry<String, String>> mapItr = values.entrySet().iterator();
        while (mapItr.hasNext()) {
            Map.Entry<String, String> entry = mapItr.next();
            LOG.info(entry.getKey() + " : " + entry.getValue());
            files.add(entry.getValue());
        }

        return files;
    }

    public List<String> buildFromXsd(final List<String> xsdFiles) throws Exception {
        List<String> files = new ArrayList<>();

        Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("xs", "http://www.w3.org/2001/XMLSchema");

        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);

        for (String file : xsdFiles) {
            Path path = Paths.get(file).getParent();
            LOG.info("XSD File: " + file);
            XdmNode booksDoc = builder.build(new File(file));
            XPathSelector selector = xpath.compile("//xs:schema").load();
            selector.setContextItem(booksDoc);

            for (XdmItem item : selector) {
                XdmSequenceIterator itr = ((XdmNode) item).axisIterator(Axis.CHILD, XS_IMPORT);
                while (itr.hasNext()) {
                    XdmNode importEl = (XdmNode) itr.next();
                    String schemaLocation = importEl.getAttributeValue(new QName("schemaLocation"));
                    String filePath = path.resolve(schemaLocation).normalize().toString();
                    // resolve to base directory
                    if (!xsdFiles.contains(filePath)) files.add(filePath);
                }
            }
        }

        // recursively traverse new found xsd files.
        if (files.size() > 0) {
            LOG.info("Found #" + files.size());
            files.addAll(buildFromXsd(files));
        }

        files.addAll(xsdFiles);
        return files;
    }
}
