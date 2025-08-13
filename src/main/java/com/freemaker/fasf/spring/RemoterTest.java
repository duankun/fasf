package com.freemaker.fasf.spring;

import com.alibaba.fastjson2.JSON;
import com.freemaker.fasf.model.ro.OrderInfoRO;
import com.freemaker.fasf.model.vo.OrderInfoVO;
import com.freemaker.fasf.remoter.OrderInfoRemoter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class RemoterTest implements ApplicationContextAware {
    @Autowired
    private OrderInfoRemoter orderInfoRemoter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        String result = animal.remote("1234");
//        System.out.println(result);
        OrderInfoVO orderInfo = orderInfoRemoter.getOrderInfo(new OrderInfoRO("123456789"));
        System.out.println(JSON.toJSONString(orderInfo));
    }
}
