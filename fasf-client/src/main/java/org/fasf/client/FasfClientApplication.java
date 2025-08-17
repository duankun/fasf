package org.fasf.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 本module是用作fasf-api的测试，实现了百度地图的小客车标准导航接口调用，测试前需要在
 * {@link org.fasf.client.service.impl.BaiduMapServiceImpl} 填入百度地图ak
 *
 * @see <a href="https://lbs.baidu.com/faq/api?title=webapi/passenger-car-apinavigate-base">百度地图开放平台</a>
 */
@SpringBootApplication
public class FasfClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(FasfClientApplication.class, args);
    }
}
