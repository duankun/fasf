package org.fasf.api;

import org.fasf.core.annotation.Api;
import org.fasf.core.annotation.QueryParam;
import org.fasf.core.annotation.Request;
import org.fasf.core.http.HttpMethod;
import reactor.core.publisher.Mono;

@Api(endpoint = "https://restapi.amap.com/")
//@Interceptors(requestInterceptors = {TraceIdInterceptor.class})
public interface AMapApi {
    @Request(path = "/v3/config/district/", method = HttpMethod.GET)
    Mono<String> districtAsync(@QueryParam("key") String key, @QueryParam("keywords") String keywords, @QueryParam("subdistrict") String subdistrict);

    @Request(path = "/v3/config/district", method = HttpMethod.GET)
    String district(@QueryParam("key") String key, @QueryParam("keywords") String keywords, @QueryParam("subdistrict") String subdistrict);
}
