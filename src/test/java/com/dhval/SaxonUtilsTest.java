package com.dhval;

import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class SaxonUtilsTest {

    private String expression1 = "//*[local-name()='OffenderEvent']/*/*[local-name()='EventName']='ProjctdRel'";
    private String expression2 = "//*[local-name()='OffenderEvent']/*/*[local-name()='EventName'" +
            " and " +
            "(text()='Reception' or text()='Transfer' or text()='ProjctdRel')]";

    // [local-name()='EventMessage']/*/*
    // ='ERInmate'

    private String expression3 = "//*[local-name()='DocumentSubjectText']='ERInmate'";
    private String expression4 =
            "//*[local-name()='DocumentSubjectText']='ERInmate'" +
            " and " +
            "//*[local-name()='ActivityCategoryText']='County Inmate Maintenance Message'";

    @Test
    public void extractXpathToFile() throws Exception {
        String outPath = "/Users/dhval/projects/github/xsl-tool/tmp/DHS_StateOffenderEvent/";
        String[] files = SaxonUtils.filesMatchingXpath("/Users/dhval/projects/github/xsl-tool/dhs-state-offender-event/",
                new String[] {expression2});
        for(String file: files) {
            XPathSelector selector1 = SaxonUtils.getXPathSelector(file, "//*[local-name()='InmateNumberID']/text()");
            XdmValue xdmValue1 = selector1.evaluate();
            String inmateId = xdmValue1.getUnderlyingValue().getStringValue();
            XPathSelector selector2 = SaxonUtils.getXPathSelector(file, "//*[local-name()='EventName']/text()");
            XdmValue xdmValue2 = selector2.evaluate();
            String eventName = xdmValue2.getUnderlyingValue().getStringValue();
            String createdTime = SaxonUtils.getXPathSelector(file, "//*[local-name()='Created']/text()").evaluate().getUnderlyingValue().getStringValue();
            if (createdTime.contains("2017-12-15"))
                SaxonUtils.extractXpathToFile("//*[local-name()='OffenderEvent']", file, outPath + inmateId + "-" + eventName + ".xml");
        }
    }

     public void evaluateXpathBoolean() throws Exception {
        String[] files = SaxonUtils.filesMatchingXpath("tmp", new String[] {expression2});
        for(String file: files) {
            System.out.println(file);
        }
        System.out.println("Files #:" + files.length);
    }
}
