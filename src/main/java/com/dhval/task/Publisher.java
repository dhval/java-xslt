package com.dhval.task;

import com.dhval.postman.SOAPClient;
import com.dhval.utils.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("Publisher")
public class Publisher extends Task {
    private static final Logger LOG = LoggerFactory.getLogger(Publisher.class);

    @Value("${client.ws.jems}")
    String clientURL;

    @Value("${data.config:config/config.json}")
    String configJson;

    @Autowired
    TaskExecutor taskExecutor;

    private String[] files;
    private List<Map<String, String>> profiles;


    @PostConstruct
    private void configure() {
        try {
            Map readValue = new ObjectMapper().readValue(new File(configJson), Map.class);
            Map data = (Map) readValue.get("data");
            String directory = (String) data.get("directory");
            files = FileUtils.allFilesByType(directory, "xml");
            profiles = (List<Map<String, String>>) data.get("profiles");
            LOG.info("D!" + data.get("name"));
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public Task init(String config) {
        return this;
    }

    public void run() throws Exception {
        for (String filePath : files) {
        for(Map<String, String> map : profiles) {
            LOG.info("Profile: " + map.get("profile"));
            LOG.info("Endpoint: " + map.get("endpoint"));
            final Map<String, String> queryMap = queryMap(map.get("endpoint"), map.get("profile"));
            SOAPClient client = new SOAPClient(clientURL) {
                public ResponseEntity<String> post(String filePath) throws Exception {
                    return transform(queryMap, new ClassPathResource("xsl/notify.xsl").getFile(),
                            new File(filePath));
                }
            };
               addTask(client, filePath);
            }
        }
        waitForThreadPool(((ThreadPoolTaskExecutor) taskExecutor));
    }

    private Map<String, String> queryMap(String url, String agency) {
        return Stream.of(
                new AbstractMap.SimpleEntry<>("Agency", agency),
                new AbstractMap.SimpleEntry<>("Target", url)
        ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
    }

    private void addTask(final SOAPClient client, String filePath){
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ResponseEntity<String> response = null;
                LOG.info("File- " + filePath);
                try {
                    response = client.post(filePath);
                } catch (Exception e) {
                    LOG.warn("Continue on Error! ...." , e);
                }
                if (response != null)
                    LOG.info("Serialized result: " + response.toString());
            }
        });
    }

    protected void waitForThreadPool(final ThreadPoolTaskExecutor threadPoolExecutor)
    {
        threadPoolExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.getThreadPoolExecutor().awaitTermination(180, TimeUnit.SECONDS);
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
