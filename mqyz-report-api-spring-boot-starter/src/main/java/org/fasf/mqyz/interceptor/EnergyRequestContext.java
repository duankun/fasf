package org.fasf.mqyz.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.fasf.core.util.JSON;
import org.fasf.mqyz.autoconfigure.FasfApiProperties;
import org.fasf.sctel.interceptor.AbstractRequestContext;
import org.fasf.sctel.interceptor.CodeType;
import org.fasf.sctel.util.SMUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class EnergyRequestContext extends AbstractRequestContext {
    private final FasfApiProperties fasfApiProperties;
    private final RestTemplate restTemplate;

    public EnergyRequestContext(RestTemplate restTemplate,FasfApiProperties fasfApiProperties) {
        this.restTemplate = restTemplate;
        this.fasfApiProperties = fasfApiProperties;
    }
    @Override
    public String getSm2PublicKey() {
        return fasfApiProperties.getEnergy().getSm2PublicKey();
    }
    @Override
    public String getEndpoint() {
        return fasfApiProperties.getEnergy().getEndpoint();
    }

    @Override
    public JsonNode login(String sm4Key) {
        String userName = fasfApiProperties.getEnergy().getUsername();
        String password = fasfApiProperties.getEnergy().getPassword();
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
        return JSON.readTree(decryptedResult);
    }

}
