package com.dhval.diff;

import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.util.*;

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

    private static void build(XdmNode srcRoot, XMLNode targetRoot) {
        Map<String, String> xPathMap = new HashMap<>();
        List<Map.Entry<XMLNode, XdmNode>> list = new ArrayList<>();
        list.add(new AbstractMap.SimpleEntry<XMLNode, XdmNode>(targetRoot, srcRoot));
        while (!list.isEmpty()) {
            Map.Entry<XMLNode, XdmNode> entry = list.remove(0);
            XMLNode targetNode = entry.getKey();
            XdmNode srcNode = entry.getValue();
            targetNode.name = srcNode.getNodeName().toString();
            XdmSequenceIterator iterator = srcNode.axisIterator(Axis.CHILD);
            while (iterator.hasNext()) {
                XdmItem item = iterator.next();
                XdmNode childNode = (XdmNode) item;
                //ignore non element nodes
                if (childNode.getNodeKind().compareTo(XdmNodeKind.ELEMENT) != 0) continue;
                XMLNode copyToNode = new XMLNode();
                copyToNode.path = getXpath(xPathMap, targetNode.path + "/" + childNode.getNodeName().toString());
                targetNode.getChildren().add(copyToNode);
                list.add(new AbstractMap.SimpleEntry<XMLNode, XdmNode>(copyToNode, childNode));
            }
        }
    }

    private static String getXpath(Map<String, String> map, String key) {
        if (StringUtils.isEmpty(key))
            return "";
        int counter = 1;
        String uniqueKey = key + "[" + counter + "]";
        while(map.containsKey(uniqueKey)) {
            uniqueKey = key + "[" + ++counter + "]";
        }
        map.put(uniqueKey, key);
        return uniqueKey;
    }

    public static List<XMLNode> getLeafNodes(XMLNode root) {
        List<XMLNode> result = new ArrayList<>();
        List<XMLNode> list = new ArrayList<>();
        list.addAll(root.getChildren());
        while (!list.isEmpty()) {
            XMLNode node = list.remove(0);
            if (node.getChildren().isEmpty()) {
                result.add(node);
            } else {
                list.addAll(node.getChildren());
            }
        }
        return result;
    }

    public static List<XMLNode> compare(XMLNode src, XMLNode target) {
        List<XMLNode> srcList =  getLeafNodes(src);
        List<XMLNode> targetList = getLeafNodes(target);
        List<XMLNode> result = new LinkedList<>(srcList);
        result.removeAll(targetList);
        return result;
    }
}
