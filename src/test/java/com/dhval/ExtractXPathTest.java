package com.dhval;

import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ExtractXPathTest {

    @Test
    public void run() throws SaxonApiException, FileNotFoundException {
        // "<copy>{//*[local-name()='Body']/*[1]}</copy>"
        //       XPathSelector _xPathSelector =  SaxonUtils.getXPathSelector(FILE, "//*[local-name()='DocumentSubjectText']");
        //       SaxonUtils.extractXpathToFile("//*[local-name()='Body']/*[1]", FILE, OUT_FILE);

        /**
         <jnet:HeaderField>
         <jnet:HeaderName>DocketNumberText</jnet:HeaderName>
         <jnet:HeaderNamespaceURI>http://jnet.state.pa.us/jxdm/aopc/wsa-ref</jnet:HeaderNamespaceURI>
         <jnet:HeaderValueText>MJ-51301-CR-0000195-2017</jnet:HeaderValueText>
         </jnet:HeaderField>

         [jnet:HeaderName='DocketNumberText']

         [namespace-uri()='http://www.jnet.state.pa.us/niem/JNET/jnet-core/1' and local-name()='HeaderName']

         /SOAP-ENV:Envelope/SOAP-ENV:Body[1]/*[namespace-uri()='http://jnet.state.pa.us/message/jnet/EventMessageConsumer/1'
         and local-name()='Notify'][1]/*[namespace-uri()='http://www.jnet.state.pa.us/niem/JNET/jnet-core/1' and local-name()='EventMessage'][1]
         /*[namespace-uri()='http://www.jnet.state.pa.us/niem/JNET/jnet-core/1' and local-name()='HeaderField'][9]/*[namespace-uri()='http://www.jnet.state.pa.us/niem/JNET/jnet-core/1' and local-name()='HeaderValueText'][1]
         */


  /*      Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();

        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode xmlDoc = builder.build(new File("data.xml"));
        XPathSelector xPathSelector = xpath.compile("//row/column[1]/value[1]").load();
        xPathSelector.setContextItem(xmlDoc);
*/
        XPathSelector selector = SaxonUtils.getXPathSelector(Constants.TMP_DATA_FILE, "//row");

        for (XdmItem rowItem : selector) {
            XdmSequenceIterator columnItr = ((XdmNode) rowItem).axisIterator(Axis.CHILD, new QName("column"));
            if (columnItr.hasNext()) {
                XdmNode column = (XdmNode) columnItr.next();
                XdmSequenceIterator valueItr = column.axisIterator(Axis.CHILD, new QName("value"));
                if (valueItr.hasNext()) {
                    String val = valueItr.next().getStringValue();
                    System.out.println(val);
                }
            }

        }
    }

}
