package com.dhval.task;

import com.dhval.config.JSONConfig;
import com.dhval.postman.SOAPClient;
import com.dhval.utils.FileUtils;
import com.dhval.utils.SaxonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("Publisher")
public class Publisher extends Task {
    private static final Logger LOG = LoggerFactory.getLogger(Publisher.class);

    @Autowired
    private JSONConfig config;

    @Autowired
    TaskExecutor taskExecutor;

    private String clientURL;
    private String xsltFilePath;
    private List<String> xpathExpression;
    private List<Map<String, String>> profiles;
    private String[] files;


    @PostConstruct
    private void configure() {
        try {
            Map cfg = config.getJson();
            Map data = (Map) cfg.get("data");
            clientURL = (String) cfg.get("endpoint");
            String directory = (String) cfg.get("directory");
            xsltFilePath = (String) cfg.get("xslt-path");
            xpathExpression = (List<String>) cfg.get("xpath-expression");
            profiles = (List<Map<String, String>>) data.get("profiles");
            files = SaxonUtils.filesMatchingXpath(directory, xpathExpression.toArray(new String[0]));
            //   FileUtils.allFilesByType(directory, "xml");
            LOG.info("D!" + cfg.get("name") + xpathExpression + " files#" + files.length);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public Task init(String config) {
        return this;
    }

    @Scheduled(initialDelay = 3000, fixedDelay = 300000L)
    public void run() throws Exception {
        List<Future<ResponseEntity<String>>> futures = new ArrayList<>();
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) taskExecutor;

        for (String filePath : files) {
            for(Map<String, String> map : profiles) {
                //final Map<String, String> queryMap = queryMap(map.get("endpoint"), map.get("profile"));
                CallablePublisher publisher = new CallablePublisher(map, clientURL, filePath, xsltFilePath);
                Future<ResponseEntity<String>> future = executor.submit(publisher);
                futures.add(future);
            }
        }
        if (!config.isEnableScheduler()) waitForThreadPool(executor);

        for(Future<ResponseEntity<String>> future : futures) {
            ResponseEntity<String> response = future.get();
            if (response != null)
                LOG.info("Serialized result: " + response.toString());
        }
    }

    private Map<String, String> queryMap(String url, String agency) {
        return Stream.of(
                new AbstractMap.SimpleEntry<>("Agency", agency),
                new AbstractMap.SimpleEntry<>("Target", url)
        ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
    }

    private void waitForThreadPool(ThreadPoolTaskExecutor taskExecutor) {
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.shutdown();
        try {
            taskExecutor.getThreadPoolExecutor().awaitTermination(180, TimeUnit.SECONDS);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    private void destroy() {
        if (files !=null) LOG.info("Files processed: " + files.length);
        if (profiles !=null) LOG.info("Profiles# " + profiles.size());
    }

}
