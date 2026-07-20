package org.fasf.sctel.interceptor;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fasf.sctel.util.SMUtils;
import org.springframework.beans.factory.DisposableBean;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
public abstract class AbstractRequestContext implements DisposableBean {
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private volatile String sm4Key;
    private volatile String accessToken;
    private volatile String tenantId;

    @PostConstruct
    private void init() {
        log.info("init energy api context");
        refreshContext();
        scheduledExecutorService.scheduleAtFixedRate(this::refreshContext, 10, 10, TimeUnit.HOURS);
    }

    private void refreshContext() {
        log.info("refresh energy api context");
        sm4Key = SMUtils.generateSM4Key()[1];
        JsonNode jsonNode = login(sm4Key);
        accessToken = jsonNode.get("access_token").asText();
        tenantId = jsonNode.get("tenantId").asText();
    }

    @Override
    public void destroy() throws Exception {
        log.info("destroy energy api context");
        scheduledExecutorService.shutdown();
    }

    public abstract JsonNode login(String sm4Key);

    public abstract String getSm2PublicKey();

    public abstract String getEndpoint();

}
