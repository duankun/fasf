package org.fasf.api;

import org.fasf.model.ro.OrderInfoRO;
import org.fasf.model.vo.OrderInfoVO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApiTest implements ApplicationContextAware {
    @Autowired
    private OrderInfoApi orderInfoApi;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        OrderInfoVO orderInfo = orderInfoApi.getOrderInfo(new OrderInfoRO("123456789"));
        orderInfoApi.getOrderInfo("fasf=端口");
    }
}
