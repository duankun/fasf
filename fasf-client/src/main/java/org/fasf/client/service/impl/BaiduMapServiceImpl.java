package org.fasf.client.service.impl;

import org.fasf.api.BaiduMapApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaiduMapServiceImpl implements InitializingBean {
    @Autowired
    private BaiduMapApi baiduMapApi;

    @Override
    public void afterPropertiesSet() throws Exception {
        String origin = "30.70,104.08";//成都火车北站
        String destination = "30.61,104.08";//成都火车南站
        String plate_number = "川A12345";
        System.out.println(baiduMapApi.routePlan("your ak", plate_number, origin, destination));
    }
}
