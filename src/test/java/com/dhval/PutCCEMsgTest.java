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
            ResponseEntity<String> response = post.post(queryMap);
            System.out.println("Serialized result: " + response.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void run() throws Exception {
         IntStream.range(1, 5).forEach( county -> {
                         IntStream.range(1, 5).forEach(actionId -> {
                             postJson(county, actionId);
                         });
                 }
         );
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
            "CaseInitiationPublish",
            "CaseInitiationPublish",
            "CaseInitiationPublish",
            "CaseInitiationPublish"
    };
}
