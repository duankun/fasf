package org.fasf.api.remoter;

import org.fasf.annotation.GetParam;
import org.fasf.annotation.Interceptors;
import org.fasf.annotation.Remoter;
import org.fasf.annotation.Request;
import org.fasf.http.HttpMethod;
import org.fasf.interceptor.AuthInterceptor;
import org.fasf.interceptor.encrypt.AESEncryptRequestInterceptor;
import org.fasf.api.model.ro.OrderInfoRO;
import org.fasf.api.model.vo.OrderInfoVO;


@Remoter(endpoint = "http://localhost:8082/summerboot")
public interface OrderInfoRemoter {

    @Request(path = "/getOrderInfo")
    @Interceptors(value = {AuthInterceptor.class, AESEncryptRequestInterceptor.class})
    OrderInfoVO getOrderInfo(OrderInfoRO orderInfoRO);

    @Request(path = "/get", method = HttpMethod.GET)
    @Interceptors(interceptors = {AuthInterceptor.class, AESEncryptRequestInterceptor.class})
    String get(@GetParam("userName")String userName);
}
