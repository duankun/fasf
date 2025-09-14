package org.fasf.client;

import org.fasf.api.AMapApi;
import org.fasf.api.OrderInfoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

/**
 * 本module是用作fasf-api的测试，实现了高德地图的行政区域查询接口调用
 *
 * @see <a href="https://lbs.amap.com/api/webservice/guide/api/district">高德地图开放平台-行政区域查询</a>
 */
@SpringBootApplication
public class FasfClientApplication {
    @Autowired
    private AMapApi aMapApi;
    @Autowired
    private OrderInfoApi orderInfoApi;

    public static void main(String[] args) {
        SpringApplication.run(FasfClientApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            String district = aMapApi.district("your amapapi key", "四川", "1");
            System.out.println(district);
            Mono<String> districtAsync = aMapApi.districtAsync("your amapapi key", "四川", "1");
            System.out.println("block" + districtAsync.block());

//            Mono<OrderInfoVO> mono = orderInfoApi.getOrderInfo("12345");
//            mono.flux().subscribe(data -> System.out.println(data),err -> System.out.println(err),() -> System.out.println("complete success"));
//            System.out.println(JSON.toJson(mono.block()));
//
//            OrderInfoVO orderInfo = orderInfoApi.getOrderInfo(new OrderInfoRO("12345"));
//            System.out.println(JSON.toJson(orderInfo));
        };
    }
}
