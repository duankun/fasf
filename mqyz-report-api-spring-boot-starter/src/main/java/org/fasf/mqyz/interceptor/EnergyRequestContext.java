package org.fasf.mqyz.interceptor;

import cn.hutool.json.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fasf.mqyz.autoconfigure.FasfApiProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EnergyRequestContext implements DisposableBean {
    private final FasfApiProperties fasfApiProperties;
    private final RestTemplate restTemplate;
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    @Getter
    private volatile String sm4Key;
    @Getter
    private volatile String accessToken;
    @Getter
    private volatile String tenantId;

    public EnergyRequestContext(RestTemplate restTemplate,FasfApiProperties fasfApiProperties) {
        this.restTemplate = restTemplate;
        this.fasfApiProperties = fasfApiProperties;
    }

    public String getSm2PublicKey() {
        return fasfApiProperties.getEnergy().getSm2PublicKey();
    }

    public String getEndpoint() {
        return fasfApiProperties.getEnergy().getEndpoint();
    }

    @PostConstruct
    private void init() {
        log.info("init energy api context");
        refreshContext();
        scheduledExecutorService.scheduleAtFixedRate(this::refreshContext, 10, 10, TimeUnit.HOURS);
    }

    private void refreshContext() {
        log.info("refresh energy api context");
        sm4Key = SMUtils.generateSM4Key()[1];
        JSONObject jsonObject = login(sm4Key);
        accessToken = jsonObject.getStr("access_token");
        tenantId = jsonObject.getStr("tenantId");
    }

    public JSONObject login(String sm4Key) {
        String userName = "sanfangjc";
        String password = "P@ss2025";
        String code = "123456";
        String grant_type = "password";
        String scope = "server";
        String verifyCodeType = "L";
        String content = "username=" + userName + "&password=" + password + "&code=" + code + "&grant_type=" + grant_type + "&scope=" + scope + "&verifyCodeType=" + verifyCodeType;
        log.info("content:{}", content);
        String queryString = SMUtils.SM4Encrypt(content, sm4Key, CodeType.Hex);
        log.info("queryString:{}", queryString);
        String url = getEndpoint() + "/dc/user/auth/oauth2/token?queryChain=" + queryString;
        log.info("url:{}", url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("Authorization", "Basic c2N0ZWxjcDE6Y2NhMmQzYWU3YzU0YmY4ZTM4NTM=");
        headers.add("Message-Sign", SMUtils.SM2Encrypt(sm4Key, getSm2PublicKey(), CodeType.Hex));
        RequestEntity<String> requestEntity = RequestEntity.post(url).headers(headers).body("");
        String result = restTemplate.exchange(requestEntity, String.class).getBody();
        log.info("result:{}", result);
        String decryptedResult = SMUtils.SM4Decrypt(result, sm4Key);
        log.info("decryptedResult:{}", decryptedResult);
        return new JSONObject(decryptedResult);
    }

    @Override
    public void destroy() throws Exception {
        log.info("destroy energy api context");
        scheduledExecutorService.shutdown();
    }
}
