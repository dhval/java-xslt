package com.dhval;

import com.dhval.postman.QueryTransactionLog;
import com.dhval.utils.FileUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class QueryTransactionLogTest {
    private static final Logger LOG = LoggerFactory.getLogger(QueryTransactionLogTest.class);

    @Autowired
    QueryTransactionLog query;

    private String element = "InmateNumberID";
    private String value = "EV7400";

    @Test
    public void queryForAvailableData1() throws Exception {
       List<String> values = query.queryForAvailableData(Stream.of(
               new AbstractMap.SimpleEntry<>("Element", element),
               new AbstractMap.SimpleEntry<>("Value", value)
       ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
       LOG.info(values.toString());
       query.saveFilesToDisk(values, "dhs-state-offender-event/");
    }

    @Test
    public void queryForAvailableData2() throws Exception {
        List<String> values = query.queryForAvailableData(Stream.of(
                new AbstractMap.SimpleEntry<>("QueryType", "StateOffenderEvent"),
                new AbstractMap.SimpleEntry<>("Element", element),
                new AbstractMap.SimpleEntry<>("Value", value)
        ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
        LOG.info(values.toString());
        query.saveFilesToDisk(values, "doc-state-offender-event/");
    }

    //@Test
    public void queryById() throws Exception {
       List<String> files = FileUtils.readFileByLine("/Users/dhval/Desktop/export.txt");
       query.saveFilesToDisk(files, "dhs-state-offender-event/");
    }


}
