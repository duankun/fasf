package com.freemaker.fasf.remoter;

import com.freemaker.fasf.annotation.GetParam;
import com.freemaker.fasf.annotation.Interceptors;
import com.freemaker.fasf.annotation.Remoter;
import com.freemaker.fasf.annotation.Request;
import com.freemaker.fasf.model.ro.OrderInfoRO;
import com.freemaker.fasf.model.vo.OrderInfoVO;
import com.freemaker.fasf.http.HttpMethod;
import com.freemaker.fasf.interceptor.AuthInterceptor;
import com.freemaker.fasf.interceptor.AESEncryptInterceptor;


@Remoter(endpoint = "http://localhost:8082/summerboot")
public interface OrderInfoRemoter {

    @Request(path = "/getOrderInfo")
    @Interceptors(interceptors = {AuthInterceptor.class, AESEncryptInterceptor.class})
    OrderInfoVO getOrderInfo(OrderInfoRO orderInfoRO);

    @Request(path = "/get", method = HttpMethod.GET)
    @Interceptors(interceptors = {AuthInterceptor.class})
    String get(@GetParam("userName")String userName);
}
