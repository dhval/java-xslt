package com.dhval.utils;

import com.dhval.Application;
import net.sf.saxon.s9api.XdmValue;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.io.FileUtils;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

public class XMLDiff {
    private static final Logger LOG = LoggerFactory.getLogger(XMLDiff.class);

    private String src;
    private String target;
    private final Diff diff;

    public static XMLDiff XMLDiffBuilder(String src, String target, String rootXPath) {
        try {
            XdmValue xdmValue1 = SaxonUtils.getXPathSelector(src, rootXPath).evaluate();
            XdmValue xdmValue2 = SaxonUtils.getXPathSelector(target, rootXPath).evaluate();
            return new XMLDiff(xdmValue1.toString(), xdmValue2.toString());
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        return null;
    }

    private XMLDiff(String src, String target) {
        this.src = src;
        this.target = target;
        diff = DiffBuilder
                .compare(src)
                .withTest(target).ignoreComments().ignoreWhitespace().normalizeWhitespace()
                .withComparisonController(ComparisonControllers.Default)
                .build();
    }

    private static Set<ComparisonType> ignoreType() {
        return Arrays.stream(new ComparisonType[] {ComparisonType.TEXT_VALUE}).collect(Collectors.toSet());
    }

    private static Set<ComparisonType> selectType() {
        return Arrays.stream(new ComparisonType[] {
                ComparisonType.CHILD_LOOKUP,
                ComparisonType.TEXT_VALUE
        }).collect(Collectors.toSet());
    }

    public Long possibleDifferenceCount() throws Exception {
        return StreamSupport
                .stream(diff.getDifferences().spliterator(), false)
                .filter( difference -> difference.getComparison().getType().equals(ComparisonType.CHILD_NODELIST_LENGTH))
                .count();
    }

    public void compare() throws Exception {
        for (Difference diff: diff.getDifferences()) {
            ComparisonResult result = diff.getResult();
            Comparison comparison = diff.getComparison();
          if (!selectType().contains(comparison.getType())) continue;
            if (ComparisonType.TEXT_VALUE.equals(comparison.getType()))
                printTextCompare(comparison);
            else
                printChildNodeList(comparison);
        }

    }

    private void printTextCompare(Comparison comparison) {
        LOG.info("----" + comparison.getControlDetails().getValue() + "--vs--" + comparison.getTestDetails().getValue() + "---");
        LOG.info("src: " + comparison.getControlDetails().getParentXPath());
        LOG.info("target: " + comparison.getTestDetails().getParentXPath());
        LOG.info("----");
    }

    private void printChildNodeList(Comparison comparison) {
        Object val = comparison.getControlDetails().getValue();
        if (val == null || val.toString().equals("#text")) return;
        LOG.info("----" + comparison.getType() + "---" + comparison.getControlDetails().getValue() + "---");
        LOG.info("src: " + comparison.getControlDetails().getParentXPath());
        LOG.info("src: " + comparison.getControlDetails().getXPath());
        LOG.info("target: " + comparison.getTestDetails().getParentXPath());
        LOG.info("target: " + comparison.getTestDetails().getXPath());
        LOG.info("----");
    }
}
