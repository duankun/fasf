package org.fasf.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 本module是用作fasf-api的测试，实现了高德地图的行政区域查询接口调用，测试前需要在
 * {@link org.fasf.client.service.impl.AMapServiceImpl} 填入高德地图key
 *
 * @see <a href="https://lbs.amap.com/api/webservice/guide/api/district">高德地图开放平台-行政区域查询</a>
 */
@SpringBootApplication
public class FasfClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(FasfClientApplication.class, args);
    }
}
