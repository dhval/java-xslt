package com.dhval;

import com.dhval.postman.PutCCEMsg;
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
public class PutCCEMsgTest {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Autowired
    PutCCEMsg post;

    private void postJson(final Integer nbr, final Integer actionId) {
        Map<String, String> queryMap = queryMap();
        try {
            String num = Integer.toString(nbr);
            queryMap.put("CountyCode", num);
            queryMap.put("Action", "http://jnet.state.pa.us/jxdm/aopc/CourtCaseEvent/" + ACTIONS[actionId]);
            for (String path: FILES) {
                ResponseEntity<String> response = post.post(queryMap, path);
                System.out.println("Serialized result: " + response.toString());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void run() throws Exception {
         for(int i =0; i <3; i++) {
         IntStream.of(19, 49, 50, 51).forEach( county -> {
                         IntStream.range(0, 5).forEach(actionId -> {
                             postJson(county, actionId);
                         });
                 }
         );
         }
    }

    private Map<String, String> queryMap() {
        return Stream.of (
                new AbstractMap.SimpleEntry<>("MessageTimestampDateTime", "2017-10-27T00:22:49.114-04:00"),
                new AbstractMap.SimpleEntry<>("MessageID", "d61d5952bb4311e7b6578eb11f677141"),
                new AbstractMap.SimpleEntry<>("Action", "http://jnet.state.pa.us/jxdm/aopc/CourtCaseEvent/CaseInitiationPublish"),
                new AbstractMap.SimpleEntry<>("CaseTypeText", "COURT"),
                new AbstractMap.SimpleEntry<>("CourtTypeCode", "DIS"),
                new AbstractMap.SimpleEntry<>("DocketNumberText", "MJ-51301-CR-0000195-2017"),
                new AbstractMap.SimpleEntry<>("CountyCode", "53"),
                new AbstractMap.SimpleEntry<>("JurisdictionCode", "01")
        ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
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
