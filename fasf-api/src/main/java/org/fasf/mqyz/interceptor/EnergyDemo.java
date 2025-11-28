package org.fasf.mqyz.interceptor;

/**
 * @author duankun
 * @date: 2025/11/28
 */
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用超市apidemo
 * @author : test
 * @date : 2024/7/24 11:02
 */
@Slf4j
public class EnergyDemo {

    //    public static final String SM2_PUBLIC_KEY = "04a2165e44d3b1000c74e2bee295a20e2b2a8a1f0ae9d2dad66d1a794e8bf54e1b7385dc0431c5062b5939377ff7fcdd3ea805adedde93990cc88aace53533d80a";
    public static final String SM2_PUBLIC_KEY = "04a2165e44d3b1000c74e2bee295a20e2b2a8a1f0ae9d2dad66d1a794e8bf54e1b7385dc0431c5062b5939377ff7fcdd3ea805adedde93990cc88aace53533d80a";

    public static void main(String[] args) {
        String sm4Key = SMUtils.generateSM4Key()[1];
        log.info("sm4Key:{}", sm4Key);
        JSONObject login = login(sm4Key);
        log.info("login:{}", login);
        getTrend(sm4Key, login);
    }

    public static void getTrend(String sm4Key, JSONObject login) {
        String accessToken = login.getStr("access_token");
        String token = String.format("%s %s", "Bearer", accessToken);
        Long tenantId = login.getLong("tenantId");
        String url = "http://118.123.116.151:60002/energy/energy/api/energy/analyse/consumption/getTrend";
        log.info("url:{}", url);
        HttpRequest request = HttpUtil.createPost(url);
        String sign = SMUtils.SM2Encrypt(sm4Key, SM2_PUBLIC_KEY, CodeType.Hex);
        request.header("Message-Sign", sign);
        request.header("Authorization", token);
        String param = "{\"energyType\":\"2\",\"statisticsTime\":\"2027-01-01 00:00:00\",\"dateType\":\"YEAR\",\"pageNum\":1}";
        String paramEncrypt = SMUtils.SM4Encrypt(param, sm4Key, CodeType.Hex);
        String queryString = "{\"queryChain\":\"" + paramEncrypt + "\"}";
        request.body(queryString);
        log.info("Headers:{}", request.headers());
        String result = sendRequest(sm4Key, token, tenantId, request);
        log.info("getTrend:{}", result);
    }

    public static String sendRequest(String sm4Key, String token, Long tenantId, HttpRequest request) {
        request.header("Content-Type", "application/json");
        request.header("Authorization", token);
        request.header("Tenant-Id", String.valueOf(tenantId));
        String sign = SMUtils.SM2Encrypt(sm4Key, SM2_PUBLIC_KEY, CodeType.Hex);
        request.header("Message-Sign", sign);
        HttpResponse response = request.execute();
        log.info("url:{}", request.getUrl());
        log.info("Headers:{}", request.headers());
        String result = response.body();
        log.info("result:{}", result);
        return SMUtils.SM4Decrypt(result, sm4Key);
    }

    /**
     * 登录demo
     */
    public static JSONObject login(String sm4Key) {
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
        String url = "http://118.123.116.151:60002/dc/user/auth/oauth2/token?queryChain=" + queryString;
        log.info("url:{}", url);
        HttpRequest request = HttpUtil.createPost(url);
        request.header("Content-Type", "application/x-www-form-urlencoded");
        request.header("Authorization", "Basic c2N0ZWxjcDE6Y2NhMmQzYWU3YzU0YmY4ZTM4NTM=");
        String sign = SMUtils.SM2Encrypt(sm4Key, SM2_PUBLIC_KEY, CodeType.Hex);
        request.header("Message-Sign", sign);
        log.info("Headers:{}", request.headers());
        HttpResponse response = request.execute();
        String result = response.body();
        log.info("result:{}", result);
        String token = SMUtils.SM4Decrypt(result, sm4Key);
        return new JSONObject(token);
    }
}

