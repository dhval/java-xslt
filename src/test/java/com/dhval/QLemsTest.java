package com.dhval;

import com.dhval.postman.PutCCEMsg;
import com.dhval.postman.QLems;
import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class QLemsTest {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Autowired
    QLems post;

    @Test
    public void run() throws Exception {
        int max  = 0;
        XPathSelector selector = SaxonUtils.getXPathSelector("export-data.xml", "//row");

        for (XdmItem rowItem : selector) {
            //if (max++ > 5) return;

            XdmSequenceIterator columnItr1 = ((XdmNode) rowItem).axisIterator(Axis.CHILD, new QName("FirstName"));
            XdmSequenceIterator columnItr2 = ((XdmNode) rowItem).axisIterator(Axis.CHILD, new QName("LastName"));
            XdmSequenceIterator columnItr3 = ((XdmNode) rowItem).axisIterator(Axis.CHILD, new QName("DOB"));

            Map<String, String> queryMap = Stream.of (
                    new AbstractMap.SimpleEntry<>("FirstName", columnItr1.next().getStringValue()),
                    new AbstractMap.SimpleEntry<>("LastName", columnItr2.next().getStringValue()),
                    new AbstractMap.SimpleEntry<>("DOB", columnItr3.next().getStringValue())
            ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));

            try {
                ResponseEntity<String> response = post.post(queryMap);
                System.out.println("Serialized result: " + response.toString());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    private static final String[] ACTIONS = {
            "CaseInitiationPublish",
            "CalendarPublish",
            "SentencePublish",
            "CaseBindOverPublish",
            "OffensePublish",
            "LifeCyclePublish"
    };

    private static final String[] FILES = {
            "sample/CP-01-CR-0000079-2016_PublicCourtCaseEvent_93361310-data.xml",
            "sample/MJ-51301-CR-0000195-2017_CourtCaseEvent_93363352-data.xml",
            "sample/MJ-09101-TR-0000162-2013.xml",
            "sample/MJ-51301-CR-0000195-2017.xml",
            "sample/MJ-51301-CR-0000195-2017-2.xml"
    };

    /**
     CCE.INBOUND.QUEUE

     CCE.REPLY.INBOUND.QUEUE

     PUBLIC.CCE.REPLY.INBOUND.QUEUE
     */
}
