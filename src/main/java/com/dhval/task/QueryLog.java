package com.dhval.task;

import com.dhval.postman.QueryTransactionLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("QueryLog")
public class QueryLog extends Task {

    private static final Logger LOG = LoggerFactory.getLogger(QueryLog.class);

    @Autowired
    QueryTransactionLog query;

    public void run() throws Exception {
        List<String> values = query.queryForAvailableData();
        LOG.info(values.toString());
        query.saveFilesToDisk(values);
    }
}
