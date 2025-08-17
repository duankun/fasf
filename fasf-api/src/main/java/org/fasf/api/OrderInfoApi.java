package org.fasf.api;

import org.fasf.annotation.*;
import org.fasf.model.ro.OrderInfoRO;
import org.fasf.model.vo.OrderInfoVO;
import org.fasf.http.HttpMethod;
import org.fasf.interceptor.AESResponseInterceptor;
import org.fasf.interceptor.AuthorizationInterceptor;
import org.fasf.interceptor.TraceIdInterceptor;
import org.fasf.interceptor.encrypt.AESEncryptRequestInterceptor;


@Api(endpoint = "http://localhost:8082/summerboot")
@Interceptors(requestInterceptors = {TraceIdInterceptor.class, AuthorizationInterceptor.class})
public interface OrderInfoApi {

    @Request(path = "/getOrderInfo")
    @Interceptors(requestInterceptors = {AESEncryptRequestInterceptor.class}, responseInterceptor = AESResponseInterceptor.class)
    @Retryable
    OrderInfoVO getOrderInfo(OrderInfoRO orderInfoRO);

    @Request(path = "/getOrderInfo", method = HttpMethod.GET)
    String get(@GetParam("orderId") String orderId);
}
