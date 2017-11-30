package com.dhval.config;

import com.dhval.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class ContextCloseHandler implements ApplicationListener<ContextClosedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ContextCloseHandler.class);

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        LOG.info("Shop Closing  !!!");
    }
}