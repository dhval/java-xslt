package com.dhval;

import com.dhval.task.FlattenWSDL;
import com.dhval.task.Task;
import com.dhval.utils.FileUtils;
import com.dhval.utils.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Autowired
    private ApplicationContext context;

    @Value("${data.config:}")
    String configJson;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("Hello ..." + args.getNonOptionArgs() + Arrays.toString(args.getSourceArgs()));
        LOG.info("OptionNames ..." + Arrays.toString(args.getOptionNames().toArray()));

        for(String opt: args.getOptionNames()) {
            LOG.info("Option - " + opt);
            final List<String> optList = args.getOptionValues(opt);
            if (optList != null)
                for(String val: optList) {
                    LOG.info("\t " + val);
                }
        }
        final List<String> destList = args.getOptionValues("dest");

        if (args.getOptionNames().size() == 1 && args.getOptionValues("task").size() ==1) {
            String taskName = args.getOptionValues("task").get(0);
            Task task = (Task) context.getBean(taskName);
            LOG.info("Executing task from config file:" + configJson);
            task.run();
        } else if (args.getNonOptionArgs().size() == 0) {
            LOG.info("No options specified. !");
        } else if (args.getNonOptionArgs().get(0).contains("schema")) {
            String path = parseOption(args, "src");
            File schemas = Paths.get(path).resolve("schemas.xml").toFile();
            new XMLWriter().buildSchemas(schemas, FileUtils.allFilesByType(path, "xsd"));
        } else if (args.getNonOptionArgs().get(0).contains("flatten")) {
            FlattenWSDL.flatten(parseOption(args, "src"));
        }
     }

    private String parseOption(ApplicationArguments args, String opt) {
        String val = args.getOptionValues(opt).get(0);
        // Remove any escaped spaces, no need to escape since it is already quoted.
        return val.replaceAll("\\\\\\ ", " ");
    }
}
