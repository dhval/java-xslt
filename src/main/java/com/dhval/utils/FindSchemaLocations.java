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

    private Processor processor = new Processor(false);

    public List<String> buildFromWsdl(final String wsdlFile) throws SaxonApiException {
        Path path = Paths.get(wsdlFile).getParent();
        LOG.info("Wsdl Path: " + path.toString());
        List<String> files = new ArrayList<>();
        DocumentBuilder builder = processor.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode xdmNode = builder.build(new File(wsdlFile));
        List<String> xsdFiles = fileRefsInWSDL(xdmNode);
        LOG.warn("Direct schema references in WSDL, found #" + xsdFiles.size());
        for(String file : xsdFiles) {
            files.add(path.resolve(file).normalize().toString());
        }
        return files;
    }

    private List<String> fileRefsInWSDL(XdmNode xdmNode) throws SaxonApiException {
        List<String> files = new ArrayList<>();
        XPathCompiler xpath = processor.newXPathCompiler();
        xpath.declareNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
        xpath.declareNamespace("xs", "http://www.w3.org/2001/XMLSchema");
        XPathSelector selector = xpath.compile("//wsdl:types").load();
        selector.setContextItem(xdmNode);
        for (XdmItem item : selector) {
            XdmSequenceIterator itr = ((XdmNode) item).axisIterator(Axis.CHILD, XS_SCHEMA);
            while (itr.hasNext()) {
                XdmNode schemaEl = (XdmNode) itr.next();
                XdmSequenceIterator itr2 = schemaEl.axisIterator(Axis.CHILD, XS_IMPORT);
                while (itr2.hasNext()) {
                    XdmNode importEl = (XdmNode) itr2.next();
                    // String namespace = importEl.getAttributeValue(new QName("namespace"));
                    String schemaLocation = importEl.getAttributeValue(new QName("schemaLocation"));
                    // resolve to base directory
                    files.add(schemaLocation);
                }
            }
        }
        return files;
    }

    /**
     * Find all the files referenced by input files, similar to breadth first graph search.
     * @param xsdFiles
     * @return
     * @throws Exception
     */
    public List<String> buildFromXsd(final List<String> xsdFiles) throws SaxonApiException {
        List<String> files = new ArrayList<>();
        DocumentBuilder builder = processor.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        int fileCounter = 0;

        while (xsdFiles.size() > 0) {
            String file = xsdFiles.get(0);
            Path path = Paths.get(file).getParent();
            // resolve to base directory
            String curFilePath = path.resolve(file).normalize().toString();
            if (files.contains(curFilePath)) {
                xsdFiles.remove(curFilePath);
                continue;
            }
            LOG.info("XSD File" + (++fileCounter) + "#: " + file);
            // mark file as visited
            files.add(curFilePath);
            xsdFiles.remove(curFilePath);
            // find all files referenced by this file
            XdmNode xdmNode = builder.build(new File(file));
            List<String> fileRefs = fileRefsInXSD(xdmNode);
            for(String f : fileRefs) {
                String filePath = path.resolve(f).normalize().toString();
                if (files.contains(filePath)) {
                    xsdFiles.remove(filePath);
                    continue;
                } else if (!xsdFiles.contains(filePath)) {
                    // visit this later
                    xsdFiles.add(filePath);
                }
            }
        }
        files.addAll(xsdFiles);
        return files;
    }

    /**
     * Find all files referenced using selector  //xs:schema/xs:import[@schemaLocation]
     * //TODO xpath gets compiled a lot.
     * @param xdmNode
     * @return
     * @throws SaxonApiException
     */
    private List<String> fileRefsInXSD(XdmNode xdmNode) throws SaxonApiException {
        List<String> files = new ArrayList<>();
        XPathCompiler xpath = processor.newXPathCompiler();
        xpath.declareNamespace("xs", "http://www.w3.org/2001/XMLSchema");
        XPathSelector selector = xpath.compile("//xs:schema").load();
        selector.setContextItem(xdmNode);
        for (XdmItem item : selector) {
            XdmSequenceIterator itr = ((XdmNode) item).axisIterator(Axis.CHILD, XS_IMPORT);
            while (itr.hasNext()) {
                XdmNode importEl = (XdmNode) itr.next();
                String schemaLocation = importEl.getAttributeValue(new QName("schemaLocation"));
                files.add(schemaLocation);
             }
        }
        return files;
    }
}
