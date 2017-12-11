package com.dhval.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

public class ConditionalThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {

    @Autowired
    private JSONConfig config;

    // Override the TaskScheduler methods
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        if (!canRun()) {
            return null;
        }
        return super.schedule(task, trigger);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        if (!canRun()) {
            return null;
        }
        return super.schedule(task, startTime);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        if (!canRun()) {
            return null;
        }
        return super.scheduleAtFixedRate(task, startTime, period);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        if (!canRun()) {
            return null;
        }
        return super.scheduleAtFixedRate(task, period);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        if (!canRun()) {
            return null;
        }
        return super.scheduleWithFixedDelay(task, startTime, delay);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        if (!canRun()) {
            return null;
        }
        return super.scheduleWithFixedDelay(task, delay);
    }

    private boolean canRun() {
        if (config == null) {
            return false;
        }

        return config.isEnableScheduler();
    }
}