package com.dhval.diff;

import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import java.util.*;
import java.util.stream.StreamSupport;

public class XMLDiff {
    private static final Logger LOG = LoggerFactory.getLogger(XMLDiff.class);

    private String src;
    private String target;
    private String parentXPath;
    private Map<String, String> map = new HashMap<>();

    public XMLDiff(String src, String target, String parentXPath) {
        this.src = src;
        this.target = target;
        this.parentXPath = parentXPath;
    }

    public Long possibleDifferenceCount() throws Exception {
        Diff diffBuilder = DiffBuilder
                .compare(SaxonUtils.getXPathSelector(src, parentXPath).evaluate().toString())
                .withTest(SaxonUtils.getXPathSelector(target, parentXPath).evaluate().toString())
                .ignoreComments().ignoreWhitespace().normalizeWhitespace()
                .withComparisonController(ComparisonControllers.Default)
                //     .withDifferenceEvaluator(evaluator)
                .build();
        return StreamSupport
                .stream(diffBuilder.getDifferences().spliterator(), false)
                .filter( difference -> difference.getComparison().getType().equals(ComparisonType.CHILD_NODELIST_LENGTH))
                .count();
    }

    /**
    public void compare() throws Exception {
        for (Difference diff: diffBuilder.getDifferences()) {
            if(diff.getResult() == ComparisonResult.SIMILAR) continue;
            Comparison comparison = diff.getComparison();
            print(comparison);
            differences.add(comparison.getControlDetails().getXPath());
        }
    }
    **/

    public List<String> structureCompare() throws Exception {
        List<String> srcList = new ArrayList<>();
        List<String> targetList = new ArrayList<>();
        Long diffCount = possibleDifferenceCount();
        LOG.info("Approximate Differences# " + diffCount);

        if (diffCount == 0)
            return srcList;

        XdmValue srcSelector = SaxonUtils.getXPathSelector(src, parentXPath).evaluate();
        print("", srcSelector.iterator(), srcList);

        XdmValue targetSelector = SaxonUtils.getXPathSelector(target, parentXPath).evaluate();
        print("", targetSelector.iterator(), targetList);

        srcList.removeAll(targetList);

        for(String result: srcList) {
            LOG.info(map.get(result) + " - " + result);
        }

        LOG.info(" count - " + srcList.size());
        return srcList;
    }

    private void print(String parent, XdmSequenceIterator itr, List<String> srcList) {
        while (itr.hasNext()) {
            XdmItem item = itr.next();
            XdmNode node = (XdmNode) item;
            if (node.getNodeKind().compareTo(XdmNodeKind.ELEMENT) != 0) continue;
            XdmSequenceIterator iter = node.axisIterator(Axis.CHILD);
            String name = parent + "/" + node.getNodeName().toString();
            map.put(name, Integer.toString(node.getLineNumber()));
            srcList.add(name);
            print(name, iter, srcList);
        }
    }

    private void print(Comparison comparison) {
        LOG.info("----" + comparison.getType() + "---" + comparison.getControlDetails().getValue() + "---"+ comparison.getTestDetails().getValue());
        LOG.info("src: " + comparison.getControlDetails().getParentXPath());
        LOG.info("src: " + comparison.getControlDetails().getXPath());
        LOG.info("target: " + comparison.getTestDetails().getParentXPath());
        LOG.info("target: " + comparison.getTestDetails().getXPath());
        LOG.info("----");
    }
}
