package org.fasf.client.service.impl;

import org.fasf.api.OrderInfoApi;
import org.fasf.model.ro.OrderInfoRO;
import org.fasf.util.JSON;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoServiceImpl implements InitializingBean {
    @Autowired
    private OrderInfoApi orderInfoApi;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(JSON.toJson(orderInfoApi.getOrderInfo(new OrderInfoRO("12312312"))));
    }
}
