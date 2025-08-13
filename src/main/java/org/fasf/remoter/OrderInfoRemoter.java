package org.fasf.remoter;

import org.fasf.annotation.GetParam;
import org.fasf.annotation.Interceptors;
import org.fasf.annotation.Remoter;
import org.fasf.annotation.Request;
import org.fasf.model.ro.OrderInfoRO;
import org.fasf.model.vo.OrderInfoVO;
import org.fasf.http.HttpMethod;
import org.fasf.interceptor.AuthInterceptor;
import org.fasf.interceptor.AESEncryptRequestInterceptor;


@Remoter(endpoint = "http://localhost:8082/summerboot")
public interface OrderInfoRemoter {

    @Request(path = "/getOrderInfo")
    @Interceptors(interceptors = {AuthInterceptor.class, AESEncryptRequestInterceptor.class})
    OrderInfoVO getOrderInfo(OrderInfoRO orderInfoRO);

    @Request(path = "/get", method = HttpMethod.GET)
    @Interceptors(interceptors = {AuthInterceptor.class})
    String get(@GetParam("userName")String userName);
}
