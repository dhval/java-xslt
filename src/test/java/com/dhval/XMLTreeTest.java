package com.dhval;

import com.dhval.diff.XMLNode;
import com.dhval.diff.XMLTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class XMLTreeTest {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    String src = "/Users/dhval/projects/github/xsl-tool/doc-state-offender-event/S-ProjctdRel-156684864-data.xml";
    String target = "/Users/dhval/projects/github/xsl-tool/dhs-state-offender-event/StateOffenderEvent-ProjctdRel-11e0-432a-3fc4-56d0-1607-fff85ce-data.xml";
    String parentXPath = "//*[local-name()='OffenderEvent']";

    @Test
    public void compare() throws Exception {
        XMLNode srcNode = XMLTree.build(src, parentXPath);
        XMLNode targetNode = XMLTree.build(target, parentXPath);
        List<XMLNode> result = XMLTree.compare(srcNode, targetNode);
        for(XMLNode s : result) {
            LOG.info(s.path);
        }
        LOG.info("Size#" + result.size());
    }

}
