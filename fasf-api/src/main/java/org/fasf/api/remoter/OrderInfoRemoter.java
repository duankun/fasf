package org.fasf.api.remoter;

import org.fasf.annotation.GetParam;
import org.fasf.annotation.Interceptors;
import org.fasf.annotation.Remoter;
import org.fasf.annotation.Request;
import org.fasf.api.model.ro.OrderInfoRO;
import org.fasf.api.model.vo.OrderInfoVO;
import org.fasf.http.HttpMethod;
import org.fasf.interceptor.AuthorizationInterceptor;
import org.fasf.interceptor.encrypt.AESEncryptRequestInterceptor;


@Remoter(endpoint = "http://localhost:8082/summerboot",interceptors = {AuthorizationInterceptor.class})
public interface OrderInfoRemoter {

    @Request(path = "/getOrderInfo")
    @Interceptors(value = {AESEncryptRequestInterceptor.class})
    OrderInfoVO getOrderInfo(OrderInfoRO orderInfoRO);

    @Request(path = "/get", method = HttpMethod.GET)
    @Interceptors(interceptors = {AuthorizationInterceptor.class, AESEncryptRequestInterceptor.class})
    String get(@GetParam("userName")String userName);
}
