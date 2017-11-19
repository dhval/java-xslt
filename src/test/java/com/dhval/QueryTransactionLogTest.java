package com.dhval;

import com.dhval.postman.QueryTransactionLog;
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

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class QueryTransactionLogTest {
    private static final Logger LOG = LoggerFactory.getLogger(QueryTransactionLogTest.class);

    @Autowired
    QueryTransactionLog query;

    @Test
    public void run() throws Exception {
        List<String> values = query.queryForAvailableData();
        LOG.info(values.toString());
        query.saveFilesToDisk(values);
    }


}