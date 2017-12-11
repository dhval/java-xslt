package com.dhval;

import com.dhval.utils.SaxonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class SaxonUtilsTest {

    private String expression1 = "//*[local-name()='OffenderEvent']/*/*[local-name()='EventName' and (text()='Reception' or text()='Transfer' or text()='ProjctdRel')]";
    private String expression2 = "//*[local-name()='DocumentSubjectText']='ERInmate'";
    private String expression3 = "//*[local-name()='OffenderEvent']/*/*[local-name()='EventName']='ProjctdRel'";

    // [local-name()='EventMessage']/*/*
    // ='ERInmate'

    @Test
    public void evaluateXpathBoolean() throws Exception {
        String[] files = SaxonUtils.filesMatchingXpath("tmp", new String[] {expression2, expression1});
        for(String file: files) {
            System.out.println(file);
        }
        System.out.println("Files #:" + files.length);
    }
}
