package org.fasf.api.remoter;

import org.fasf.annotation.*;
import org.fasf.api.model.ro.OrderInfoRO;
import org.fasf.api.model.vo.OrderInfoVO;
import org.fasf.http.HttpMethod;
import org.fasf.interceptor.AESResponseInterceptor;
import org.fasf.interceptor.AuthorizationInterceptor;
import org.fasf.interceptor.TraceIdInterceptor;
import org.fasf.interceptor.encrypt.AESEncryptRequestInterceptor;


@Remoter(endpoint = "http://localhost:8082/summerboot")
@Interceptors(requestInterceptors = {TraceIdInterceptor.class, AuthorizationInterceptor.class})
public interface OrderInfoRemoter {

    @Request(path = "/getOrderInfo")
    @Interceptors(requestInterceptors = {AESEncryptRequestInterceptor.class}, responseInterceptor = AESResponseInterceptor.class)
    @Retryable
    OrderInfoVO getOrderInfo(OrderInfoRO orderInfoRO);

    @Request(path = "/getOrderInfo", method = HttpMethod.GET)
    String get(@GetParam("orderId") String orderId);
}
