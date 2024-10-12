package com.chainsys.tradingapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasksService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksService.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ScheduledTasksService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(cron = "0 * * * * ?")  // Every minute
    public void checkStopLossAndGain() {
        try {
            logger.info("Executing checkStopLossAndGain...");
            jdbcTemplate.update("CALL checkStopLossAndGain()");
        } catch (Exception e) {
            logger.error("Error executing checkStopLossAndGain", e);
        }
    }
}
