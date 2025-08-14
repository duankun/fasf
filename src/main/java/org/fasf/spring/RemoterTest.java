package org.fasf.spring;

import org.fasf.model.ro.OrderInfoRO;
import org.fasf.model.vo.OrderInfoVO;
import org.fasf.remoter.OrderInfoRemoter;
import org.fasf.util.JSON;
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
        System.out.println(JSON.toJson(orderInfo));
        System.out.println(orderInfoRemoter.get("fasf=端口"));
    }
}
