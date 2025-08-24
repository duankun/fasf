package org.fasf.client.service.impl;

import org.fasf.api.AMapApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

//@Service
public class AMapServiceImpl implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(AMapServiceImpl.class);
    @Autowired
    private AMapApi aMapApi;

    @Override
    public void afterPropertiesSet() throws Exception {
        String district = aMapApi.district("your amapapi key", "四川", "1");
        logger.info(district);
        Mono<String> districtAsync = aMapApi.districtAsync("your amapapi key", "四川", "1");
        logger.info(districtAsync.block());
    }

}
