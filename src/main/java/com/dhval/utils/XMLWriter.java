package com.dhval.utils;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.dom.DocumentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

public class XMLWriter {
    private static final Logger LOG = LoggerFactory.getLogger(XMLWriter.class);

    public void buildSchemas(String path) throws IOException {
        LOG.info("Path: " + path);
        String[] files = FileUtils.allFilesByType(path, "xsd");
        LOG.info("Found #" + files.length + " files.");
        for(String file: files) {
            LOG.info(file);
        }
        buildSchemas(FileUtils.allFilesByType(path, "xsd"));
    }

    public void buildSchemas(String[] files) {
        Node item;
        Document xmlDoc = new DocumentImpl();
        Element root = xmlDoc.createElement("schemas");
        for (String file : files) {
            item = xmlDoc.createElement("file");
            item.appendChild(xmlDoc.createTextNode(file));
            root.appendChild(item);
        }
        xmlDoc.appendChild(root);

        try {
            Source source = new DOMSource(xmlDoc);
            File xmlFile = new File("schemas.xml");
            StreamResult result = new StreamResult(new OutputStreamWriter(
                    new FileOutputStream(xmlFile), "UTF-8"));
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            xformer.transform(source, result);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }
}
