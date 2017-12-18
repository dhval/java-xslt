package com.dhval.diff;

import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class XMLTree {
    private static final Logger LOG = LoggerFactory.getLogger(XMLTree.class);

    public static XMLNode build(String file, String xPath)
            throws SaxonApiException, FileNotFoundException {
        return  build((XdmNode) SaxonUtils.getXPathSelector(file, xPath).evaluate());
    }

    public static XMLNode build(XdmNode root) {
        if (root == null) return null;
        XMLNode node = new XMLNode();
        build(root, node);
        return node;
    }

    private static void build(XdmNode src, XMLNode target) {
        target.name = src.getNodeName().toString();
        XdmSequenceIterator iterator = src.axisIterator(Axis.CHILD);
        while (iterator.hasNext()) {
            XdmItem item = iterator.next();
            XdmNode srcNode = (XdmNode) item;
            //ignore non element nodes
            if (srcNode.getNodeKind().compareTo(XdmNodeKind.ELEMENT) != 0) continue;
            XMLNode targetNode = new XMLNode();
            targetNode.path = target.path + "/" + target.name;
            target.getChildren().add(targetNode);
            build(srcNode, targetNode);
        }
    }

    public static List<String> getLeaves(XMLNode root, List<String> target) {
        if(root.getChildren().isEmpty()) {
            target.add(root.path + "/" + root.name);
        }
        for(XMLNode node: root.getChildren()){
            getLeaves(node, target);
        }
        return target;
    }

    public static List<String> compare(XMLNode src, XMLNode target) {
        List<String> srcList =  getLeaves(src, new LinkedList<>());;
        List<String> targetList = getLeaves(target, new LinkedList<>());
        List<String> result = new LinkedList<>(srcList);
        result.removeAll(targetList);
        return result;
    }
}
