package org.fasf.api;

import org.fasf.core.annotation.*;
import org.fasf.core.http.HttpMethod;
import org.fasf.interceptor.AESResponseInterceptor;
import org.fasf.interceptor.AuthorizationInterceptor;
import org.fasf.interceptor.TraceIdInterceptor;
import org.fasf.interceptor.encrypt.AESEncryptRequestInterceptor;
import org.fasf.model.ro.OrderInfoRO;
import org.fasf.model.vo.OrderInfoVO;
import reactor.core.publisher.Mono;


@Api(endpoint = "http://localhost:8082/summerboot")
@Interceptors(requestInterceptors = {TraceIdInterceptor.class, AuthorizationInterceptor.class})
public interface OrderInfoApi {

    @RequestMapping(path = "/getOrderInfo/{replace}")
    @Interceptors(requestInterceptors = {AESEncryptRequestInterceptor.class}, responseInterceptor = AESResponseInterceptor.class)
    @Retry
    OrderInfoVO getOrderInfo(@RequestBody OrderInfoRO orderInfoRO,@PathParam("replace") String replace,@QueryParam("key")String key);

    @RequestMapping(path = "/getOrderInfo", method = HttpMethod.GET)
    @Interceptors(requestInterceptors = {AESEncryptRequestInterceptor.class}, responseInterceptor = AESResponseInterceptor.class)
    @Retry
    Mono<OrderInfoVO> getOrderInfo(@QueryParam("orderId") String orderId);
}
