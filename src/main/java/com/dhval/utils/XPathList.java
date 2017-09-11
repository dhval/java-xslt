package com.dhval.utils;

import net.sf.saxon.s9api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XPathList {

    public List<String> list2(String path, String select, String nodeName) throws Exception {
        List<String> values = new ArrayList<>();
        Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("saxon", "http://saxon.sf.net/"); // not actually used, just for demonstration

        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode booksDoc = builder.build(new File(path));

        // find all the nodeName elements, within selector

        XPathSelector selector = xpath.compile(select).load();
        selector.setContextItem(booksDoc);

        for (XdmItem item: selector) {
            XdmSequenceIterator itr = ((XdmNode)item).axisIterator(Axis.CHILD, new QName(nodeName));
            while (itr.hasNext()) {
                XdmNode title = (XdmNode)itr.next();
                title.getAttributeValue(new QName(nodeName));
                values.add(title.getStringValue());
            }
        }

        return values;
    }

    public List<String> list(String path, String select, String nodeName) throws Exception {
        List<String> values = new ArrayList<>();
        Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("saxon", "http://saxon.sf.net/"); // not actually used, just for demonstration

        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode booksDoc = builder.build(new File(path));

        // find all the nodeName elements, within selector

        XPathSelector selector = xpath.compile(select).load();
        selector.setContextItem(booksDoc);

        for (XdmItem item: selector) {
            XdmSequenceIterator itr = ((XdmNode)item).axisIterator(Axis.CHILD, new QName(nodeName));
            while (itr.hasNext()) {
                XdmNode title = (XdmNode)itr.next();
                title.getAttributeValue(new QName(nodeName));
                values.add(title.getStringValue());
            }
        }

        return values;
    }
}
