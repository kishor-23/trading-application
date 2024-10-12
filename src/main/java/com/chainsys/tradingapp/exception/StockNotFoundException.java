package com.chainsys.tradingapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(StockNotFoundException.class);

    // Constructor with message
    public StockNotFoundException(String msg) {
        super(msg);
        logger.error("StockNotFoundException: {}", msg);
    }

    // Constructor with message and cause
    public StockNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
        logger.error("StockNotFoundException: {}", msg, cause);
    }
}
