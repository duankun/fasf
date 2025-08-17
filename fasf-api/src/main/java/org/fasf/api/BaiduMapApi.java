package org.fasf.api;

import org.fasf.annotation.Api;
import org.fasf.annotation.GetParam;
import org.fasf.annotation.Interceptors;
import org.fasf.annotation.Request;
import org.fasf.http.HttpMethod;
import org.fasf.interceptor.TraceIdInterceptor;

@Api(endpoint = "https://api.map.baidu.com/")
@Interceptors(requestInterceptors = {TraceIdInterceptor.class})
public interface BaiduMapApi {
    @Request(path = "api_navigate/v1/route_plan", method = HttpMethod.GET)
    String routePlan(@GetParam("ak") String ak, @GetParam("plate_number") String plate_number, @GetParam("origin") String origin, @GetParam("destination") String destination);
}
