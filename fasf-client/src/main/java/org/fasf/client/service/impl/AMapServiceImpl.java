package org.fasf.client.service.impl;

import org.fasf.api.AMapApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AMapServiceImpl implements InitializingBean {
    @Autowired
    private AMapApi aMapApi;

    @Override
    public void afterPropertiesSet() throws Exception {
        CompletableFuture<String> district = aMapApi.district("your amapapi key", "四川", "1");
        System.out.println(district.join());
    }

}
