package com.chainsys.tradingapp.exception;

import org.springframework.dao.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PanCardDuplicateException extends DuplicateKeyException {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(PanCardDuplicateException.class);

    public PanCardDuplicateException(String msg) {
        super(msg);
        logger.error("PanCardDuplicateException: {}", msg);
    }
}
