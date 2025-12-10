package org.fasf.client;

import jakarta.annotation.Resource;
import org.fasf.api.AMapApi;
import org.fasf.api.OrderInfoApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(FasfClientApplication.class);
    @Resource
    private AMapApi aMapApi;
    @Resource
    private OrderInfoApi orderInfoApi;

    public static void main(String[] args) {
        SpringApplication.run(FasfClientApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            String district = aMapApi.district("cc316454609278510abd9b93f1fc77a4", "四川", "2");
            System.out.println(district);
//            Mono<String> districtAsync = aMapApi.districtAsync("cc316454609278510abd9b93f1fc77a4", "成都", "1");
//            districtAsync.subscribe(logger::info, System.out::println);

//            Mono<OrderInfoVO> mono = orderInfoApi.getOrderInfo("12345");
//            mono.flux().subscribe(data -> System.out.println(data),err -> System.out.println(err),() -> System.out.println("complete success"));
//            System.out.println(JSON.toJson(mono.block()));

//            OrderInfoVO orderInfo = orderInfoApi.getOrderInfo(new OrderInfoRO("12345"), "replace", "key");
//            System.out.println(JSON.toJson(orderInfo));
        };
    }
}
