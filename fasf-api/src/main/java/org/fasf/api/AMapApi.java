package org.fasf.api;

import org.fasf.core.annotation.Api;
import org.fasf.core.annotation.QueryParam;
import org.fasf.core.annotation.RequestMapping;
import org.fasf.core.http.HttpMethod;
import reactor.core.publisher.Mono;

@Api(endpoint = "https://restapi.amap.com/")
//@Interceptors(requestInterceptors = {TraceIdInterceptor.class})
public interface AMapApi {
    @RequestMapping(path = "/v3/config/district", method = HttpMethod.GET)
    Mono<String> districtAsync(@QueryParam("key") String key, @QueryParam("keywords") String keywords, @QueryParam("subdistrict") String subdistrict);

    @RequestMapping(path = "/v3/config/district", method = HttpMethod.GET)
    String district(@QueryParam("key") String key, @QueryParam("keywords") String keywords, @QueryParam("subdistrict") String subdistrict);
}
