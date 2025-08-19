package org.fasf.api;

import org.fasf.annotation.Api;
import org.fasf.annotation.GetParam;
import org.fasf.annotation.Interceptors;
import org.fasf.annotation.Request;
import org.fasf.http.HttpMethod;
import org.fasf.interceptor.TraceIdInterceptor;

import java.util.concurrent.CompletableFuture;

@Api(endpoint = "https://restapi.amap.com/")
@Interceptors(requestInterceptors = {TraceIdInterceptor.class})
public interface AMapApi {
    @Request(path = "/v3/config/district", method = HttpMethod.GET)
    CompletableFuture<String> district(@GetParam("key") String key, @GetParam("keywords") String keywords, @GetParam("subdistrict") String subdistrict);
}
