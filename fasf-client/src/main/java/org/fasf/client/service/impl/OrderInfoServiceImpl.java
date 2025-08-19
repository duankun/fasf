package org.fasf.client.service.impl;

import org.fasf.api.OrderInfoApi;
import org.fasf.model.ro.OrderInfoRO;
import org.fasf.model.vo.OrderInfoVO;
import org.fasf.util.JSON;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

//@Service
public class OrderInfoServiceImpl implements InitializingBean {
    @Autowired
    private OrderInfoApi orderInfoApi;

    @Override
    public void afterPropertiesSet() throws Exception {
        OrderInfoVO orderInfo = orderInfoApi.getOrderInfo("12345");
        System.out.println(JSON.toJson(orderInfo));

        CompletableFuture<OrderInfoVO> future = orderInfoApi.getOrderInfo(new OrderInfoRO("12345"));
        System.out.println(JSON.toJson(future.join()));
    }
}
