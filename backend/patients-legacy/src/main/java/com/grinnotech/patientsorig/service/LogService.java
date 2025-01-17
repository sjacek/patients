package com.grinnotech.patientsorig.service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.invoke.MethodHandles;
import java.util.Map;

/**
 *
 * @author Jacek Sztajnke
 */
@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    @ExtDirectMethod
    @Async
    public void logClientCrash(@RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent, Map<String, Object> crashData) {
        StringBuilder sb = new StringBuilder();
        sb.append("JavaScript Error").append(LINE_SEPARATOR).append("User-Agent: ").append(userAgent);

        crashData.forEach((k, v) -> sb.append(LINE_SEPARATOR).append(k).append(": ").append(v));

        logger.error(sb.toString());
    }

    @ExtDirectMethod
    @Async
    public void info(String message) {
        logger.info(message);
    }

    @ExtDirectMethod
    @Async
    public void debug(String message) {
        logger.debug(message);
    }
}
