package org.fasf.util;

import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.UUID;

import static org.fasf.Constants.TRACE_ID;

public class MDCUtils {
    public static void setupMDC() {
        String traceId = MDC.get(TRACE_ID);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put(TRACE_ID, traceId);
        }
    }

    public static void cleanupMDC() {
        MDC.clear();
    }

    public static void setContextMap(Map<String, String> contextMap) {
        if (CollectionUtils.isEmpty(contextMap)) {
            return;
        }
        MDC.setContextMap(contextMap);
    }
}
