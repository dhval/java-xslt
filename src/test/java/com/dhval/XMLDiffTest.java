package com.dhval;


import com.dhval.diff.XMLNode;
import com.dhval.diff.XMLDiff;
import com.dhval.diff.XMLTree;
import com.dhval.utils.SaxonUtils;
import com.dhval.diff.SmartXMLDiffEvaluator;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class XMLDiffTest {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);


    String target = "/Users/dhval/projects/github/xsl-tool/dhs-state-offender-event/StateOffenderEvent-ProjctdRel-11e0-432a-3fc4-56d0-1607-fff85ce-data.xml";
    String src = "/Users/dhval/projects/github/xsl-tool/doc-state-offender-event/S-ProjctdRel-156684864-data.xml";

    String srcDirectory = "/Users/dhval/projects/github/xsl-tool/doc-state-offender-event/";
    String targetDirectory = "/Users/dhval/projects/github/xsl-tool/dhs-state-offender-event";
    String parentXPath = "//*[local-name()='OffenderEvent']";
    String xPath = "//*[local-name()='InmateNumberID']/text()";
    String xPathMatch = "//*[local-name()='InmateNumberID']='VALUE'";

    private DifferenceEvaluator evaluator = new DifferenceEvaluator() {
        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            if(outcome == ComparisonResult.EQUAL)
                return ComparisonResult.EQUAL;
            if(comparison.getTestDetails().getXPath() != null
                    && comparison.getControlDetails().getXPath() != null
                    && comparison.getControlDetails().getXPath().equals(comparison.getTestDetails().getXPath()))
                return ComparisonResult.SIMILAR;
            return ComparisonResult.DIFFERENT;
        }
    };

    //@Test
    public void structureCompare() throws Exception {
        XMLDiff differ = new XMLDiff(src, target, parentXPath);
        differ.structureCompare();
    }

    //@Test
    public void getLeaves() throws Exception {
        XMLNode srcNode = XMLTree.build(src, parentXPath);
        List<XMLNode> result = XMLTree.getLeafNodes(srcNode);
        for(XMLNode node: result) {
            LOG.info(node.path);
        }
        LOG.info("#" + result.size());
    }

    public void run() throws Exception {
       com.dhval.utils.XMLDiff xmlDiff = com.dhval.utils.XMLDiff.XMLDiffBuilder(src, target, parentXPath);
       Long diffCount = xmlDiff.possibleDifferenceCount();
       LOG.info("Possible Differences# " + diffCount);
       Diff diff = DiffBuilder
                .compare(SaxonUtils.getXPathSelector(src, parentXPath).evaluate().toString())
                .withTest(SaxonUtils.getXPathSelector(target, parentXPath).evaluate().toString())
                .ignoreComments().ignoreWhitespace().normalizeWhitespace()
                .withComparisonController(ComparisonControllers.Default)
                .withDifferenceEvaluator(new SmartXMLDiffEvaluator())
                .build();

        for (Difference difference: diff.getDifferences()) {
            Comparison comparison = difference.getComparison();
            if(difference.getResult() == ComparisonResult.SIMILAR) continue;
            LOG.info("----");
            LOG.info("src: " + comparison.getControlDetails().getParentXPath());
            LOG.info("src: " + comparison.getControlDetails().getXPath());
            LOG.info("----");
        }

    }

    @Test
    public void runAll() throws Exception {
         Set<String> stringSet = new HashSet<>();
         String[] files = com.dhval.utils.FileUtils.allFilesByType(targetDirectory, "xml");
         int maxCounter = 1500;

        for(int i=0; i<maxCounter && i<files.length; i++ ) {
            String createdTime = SaxonUtils.getXPathSelector(files[i], "//*[local-name()='Created']/text()").evaluate().getUnderlyingValue().getStringValue();
            /**
            if (!createdTime.contains("2017-12-18")) {
                maxCounter++;
                LOG.info("Skip-" + createdTime);
                continue;
            }
             **/
            XPathSelector selector = SaxonUtils.getXPathSelector(files[i], xPath);
            XdmValue xdmValue = selector.evaluate();
            String inmateId = xdmValue.getUnderlyingValue().getStringValue();
            String expression = xPathMatch.replace("VALUE", inmateId);
            String[] matches = SaxonUtils.filesMatchingXpath(srcDirectory, new String[] {expression});
            if (matches == null || matches.length ==0) continue;

            LOG.info("------------------------------------");
            XMLNode srcNode = XMLTree.build(matches[0], parentXPath);
            XMLNode targetNode = XMLTree.build(files[i], parentXPath);
            List<XMLNode> result = XMLTree.compare(srcNode, targetNode);


            //XMLPerfectSourceDiff differ = new XMLPerfectSourceDiff(matches[0], files[i], parentXPath);
            //stringSet.addAll(differ.structureCompare());
            stringSet.addAll(result.stream().map(node -> node.path).collect(Collectors.toList()));

            LOG.info("src --- " +  matches[0]);
            LOG.info("------------------------------------");
            LOG.info("target --- " +  files[i]);

            LOG.info("------------------------------------");
            LOG.info("------------------------------------");
            LOG.info("----" + result.size() + "---------");
            LOG.info("------------------------------------");
            LOG.info("------------------------------------");

            for(XMLNode m: result)
                LOG.info(m.path);

        }

        FileWriter fw = new FileWriter("out.txt");
        for (String m: stringSet) {
            fw.write(m + "\n");
        }
        fw.close();
    }


    public static String findFileBy(String s) {
        return null;
    }
}
